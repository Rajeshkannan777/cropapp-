"""
Crop Validation Service for CropGuard AI
Handles:
- Image format and dimension validation
- File size checking
- Supported crop validation
- Leaf/plant visual feature verification heuristics
- Normalized crop matching
"""

import io
from typing import Tuple, Optional, Set
from PIL import Image, ImageStat

SUPPORTED_CROPS: Set[str] = {
    "tomato",
    "potato",
    "rice",
    "maize",
    "wheat",
    "cotton",
    "chilli",
}

# Alias mapping for alternative names / spellings
CROP_ALIASES = {
    "tomato": "Tomato",
    "tomatoes": "Tomato",
    "potato": "Potato",
    "potatoes": "Potato",
    "rice": "Rice",
    "paddy": "Rice",
    "maize": "Maize",
    "corn": "Maize",
    "wheat": "Wheat",
    "cotton": "Cotton",
    "chilli": "Chilli",
    "chili": "Chilli",
    "pepper": "Chilli",
    "hot pepper": "Chilli",
}

ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "webp"}
ALLOWED_IMAGE_FORMATS = {"JPEG", "PNG", "WEBP"}


def normalize_crop_name(name: str) -> Optional[str]:
    """Standardizes crop name to canonical title case or None if unsupported."""
    if not name:
        return None
    clean = name.strip().lower()
    return CROP_ALIASES.get(clean)


def is_supported_crop(name: str) -> bool:
    """Returns True if the crop is in the 7 supported crops."""
    norm = normalize_crop_name(name)
    return norm is not None and norm.lower() in SUPPORTED_CROPS


def validate_image_file(
    file_bytes: bytes,
    filename: Optional[str] = None,
    max_size_bytes: int = 15 * 1024 * 1024
) -> Tuple[bool, Optional[str], Optional[Image.Image]]:
    """
    Validates the uploaded file:
    1. Size within limit
    2. Real image decodable by Pillow
    3. Allowed image format (JPEG, PNG, WEBP)
    4. Minimum reasonable resolution for plant diagnosis
    """
    if not file_bytes:
        return False, "Uploaded file is empty.", None

    if len(file_bytes) > max_size_bytes:
        max_mb = max_size_bytes // (1024 * 1024)
        return False, f"Image file exceeds maximum allowable size of {max_mb} MB.", None

    # Verify filename extension if provided
    if filename:
        ext = filename.split(".")[-1].lower() if "." in filename else ""
        if ext and ext not in ALLOWED_EXTENSIONS:
            return False, f"Unsupported file extension '.{ext}'. Supported formats: JPG, JPEG, PNG, WEBP.", None

    try:
        image = Image.open(io.BytesIO(file_bytes))
        image.verify()  # Verify integrity
        # Image.verify leaves the stream consumed, reopen for processing
        image = Image.open(io.BytesIO(file_bytes))
    except Exception:
        return False, "Uploaded file is not a valid or readable image.", None

    # Format check
    fmt = (image.format or "").upper()
    if fmt not in ALLOWED_IMAGE_FORMATS:
        return False, f"Unsupported image format: {fmt}. Allowed formats: JPG, JPEG, PNG, WEBP.", None

    width, height = image.size
    if width < 32 or height < 32:
        return False, "Image resolution is too low for crop diagnosis. Minimum required is 32x32 pixels.", None

    # Check for blank / completely monotone image
    try:
        rgb_img = image.convert("RGB")
        stat = ImageStat.Stat(rgb_img)
        # stddev measures pixel variance. If near zero, image is a solid block of color
        variances = stat.var
        if all(v < 1.0 for v in variances):
            return False, "Image is blank or uniformly solid. Please upload a clear photo of a crop or leaf.", None
    except Exception:
        pass

    return True, None, image


def check_leaf_plant_heuristics(image: Image.Image) -> Tuple[bool, str]:
    """
    Pre-screening heuristic to check if the image contains typical foliage/plant color features
    (green, yellow, brown, earthy pigments).
    Helps flag obviously blank or pure-gray pictures before sending to heavy inference.
    """
    try:
        rgb_img = image.convert("RGB").resize((100, 100))
        pixels = list(rgb_img.getdata())
        plant_pixel_count = 0
        total_pixels = len(pixels)

        for r, g, b in pixels:
            # Green foliage signature: g > r and g > b
            is_green = (g > r * 0.95 and g > b * 1.05 and g > 30)
            # Yellow/chlorosis: high r and g, lower b
            is_yellow = (r > 100 and g > 100 and b < 90 and abs(r - g) < 50)
            # Brown necrotic tissue/stem: r > 50, g > 30, b < 60, r > g > b
            is_brown = (r > 40 and g > 25 and b < 70 and r >= g and g >= b)
            # Cotton white fibers / tomato red fruit / chilli red
            is_fruit_or_fiber = (r > 150 and g < 70 and b < 70) or (r > 180 and g > 180 and b > 180)

            if is_green or is_yellow or is_brown or is_fruit_or_fiber:
                plant_pixel_count += 1

        plant_ratio = plant_pixel_count / float(total_pixels)
        if plant_ratio < 0.04:
            return False, "Image lacks discernible crop or foliage colors. Please upload a clear leaf, plant, or crop image."
        return True, "Valid plant visual profile"
    except Exception:
        return True, "Check skipped"
