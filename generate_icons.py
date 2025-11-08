from PIL import Image, ImageDraw
import math

# Icon sizes for different densities
sizes = {
    'mdpi': 48,
    'hdpi': 72,
    'xhdpi': 96,
    'xxhdpi': 144,
    'xxxhdpi': 192
}

def create_icon(size, path):
    # Create image with purple background
    img = Image.new('RGBA', (size, size), (99, 102, 241, 255))  # #6366F1
    draw = ImageDraw.Draw(img)
    
    # Calculate scaling
    scale = size / 200
    center_x = size // 2
    
    # Colors
    light_purple = (233, 213, 255, 255)  # #E9D5FF
    white = (255, 255, 255, 255)
    purple = (139, 92, 246, 255)  # #8B5CF6
    
    # Draw glow effect
    for i in range(5):
        alpha = int(50 - i * 10)
        glow_radius = int(size * 0.4 + i * 5)
        draw.ellipse([center_x - glow_radius, center_x - glow_radius,
                     center_x + glow_radius, center_x + glow_radius],
                    fill=(139, 92, 246, alpha))
    
    # Book at bottom
    book_bottom = int(size * 0.85)
    book_width = int(size * 0.45)
    book_height = int(size * 0.18)
    book_top = book_bottom - book_height
    
    # Left page
    draw.polygon([
        (center_x - book_width, book_top),
        (center_x - book_width, book_bottom),
        (center_x - 2, book_bottom),
        (center_x - 2, book_top)
    ], fill=light_purple, outline=white, width=max(1, int(2*scale)))
    
    # Right page
    draw.polygon([
        (center_x + 2, book_top),
        (center_x + 2, book_bottom),
        (center_x + book_width, book_bottom),
        (center_x + book_width, book_top)
    ], fill=light_purple, outline=white, width=max(1, int(2*scale)))
    
    # Book spine
    draw.rectangle([center_x - 2, book_top, center_x + 2, book_bottom],
                  fill=white)
    
    # Book pages lines
    line_y1 = book_top + int(book_height * 0.25)
    line_y2 = book_top + int(book_height * 0.5)
    line_y3 = book_top + int(book_height * 0.75)
    
    for y in [line_y1, line_y2, line_y3]:
        draw.line([(center_x - book_width + 5, y), (center_x - 8, y)],
                 fill=purple, width=max(1, int(scale)))
        draw.line([(center_x + 8, y), (center_x + book_width - 5, y)],
                 fill=purple, width=max(1, int(scale)))
    
    # Arrow
    arrow_y = int(size * 0.5)
    arrow_size = int(size * 0.08)
    draw.polygon([
        (center_x, arrow_y - arrow_size),
        (center_x - arrow_size//2, arrow_y),
        (center_x - arrow_size//3, arrow_y),
        (center_x - arrow_size//3, arrow_y + arrow_size),
        (center_x + arrow_size//3, arrow_y + arrow_size),
        (center_x + arrow_size//3, arrow_y),
        (center_x + arrow_size//2, arrow_y),
    ], fill=white)
    
    # Brain at top
    brain_y = int(size * 0.28)
    brain_width = int(size * 0.18)
    brain_height = int(size * 0.16)
    
    # Brain outline
    draw.ellipse([center_x - brain_width, brain_y - brain_height,
                 center_x + brain_width, brain_y + brain_height],
                outline=white, width=max(2, int(3*scale)))
    
    # Brain division
    draw.line([(center_x, brain_y - brain_height), (center_x, brain_y + brain_height)],
             fill=white, width=max(2, int(2*scale)))
    
    # Brain curves (simplified)
    curve_width = int(size * 0.08)
    curve_height = int(size * 0.04)
    
    # Left curves
    draw.arc([center_x - brain_width + 2, brain_y - curve_height,
             center_x - curve_width, brain_y + curve_height],
            start=45, end=135, fill=white, width=max(2, int(2*scale)))
    
    # Right curves
    draw.arc([center_x + curve_width, brain_y - curve_height,
             center_x + brain_width - 2, brain_y + curve_height],
            start=45, end=135, fill=white, width=max(2, int(2*scale)))
    
    # Neural dots
    dot_size = max(2, int(3*scale))
    dot_positions = [
        # Left side
        (center_x - brain_width - 5, brain_y - brain_height + 5),
        (center_x - brain_width - 8, brain_y - brain_height//2),
        (center_x - brain_width - 8, brain_y),
        (center_x - brain_width - 5, brain_y + brain_height - 5),
        # Right side
        (center_x + brain_width + 5, brain_y - brain_height + 5),
        (center_x + brain_width + 8, brain_y - brain_height//2),
        (center_x + brain_width + 8, brain_y),
        (center_x + brain_width + 5, brain_y + brain_height - 5),
    ]
    
    for x, y in dot_positions:
        draw.ellipse([x - dot_size, y - dot_size, x + dot_size, y + dot_size],
                    fill=white)
    
    # Connection lines from dots
    line_length = int(size * 0.05)
    for i, (x, y) in enumerate(dot_positions):
        if i < 4:  # Left side
            draw.line([(x, y), (x - line_length, y)], fill=white, width=max(1, int(scale)))
        else:  # Right side
            draw.line([(x, y), (x + line_length, y)], fill=white, width=max(1, int(scale)))
    
    # Save
    img.save(path)
    print(f"Created: {path} ({size}x{size})")

# Generate icons for all densities
for density, size in sizes.items():
    path = f'app/src/main/res/mipmap-{density}/ic_launcher.png'
    create_icon(size, path)
    # Also create round version
    round_path = f'app/src/main/res/mipmap-{density}/ic_launcher_round.png'
    create_icon(size, round_path)
    
print("\n✅ All icons generated with detailed brain+book design!")
