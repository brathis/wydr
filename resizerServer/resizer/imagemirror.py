

#Portrait: Vertical Image:  1080px in width by 1350px in height =8:10

import numpy as np
from PIL import Image
import sys
import math
import cv2

def flip_image(image):
    """
    Flip or mirror the image
    @param image_path: The path to the image to edit
    @param saved_location: Path to save the cropped image
    """

    mirror = image.transpose(Image.FLIP_LEFT_RIGHT)
    # mirror.save(saved_location)
    # mirror.show()
    return mirror

   
def side_panel_width(image):
    width, height = image.size
    targetwidth = height*0.8
    #Vertical Image:  1080px in width by 1350px in height = 0.8 = 8:10
    side_panel_width = math.ceil((targetwidth-width) /2)
    return side_panel_width


def left_panel(mirrored, width, height, side_panel_width):
    left = width-side_panel_width
    top = 0
    right = width
    bottom = height
    left_panel = mirrored.crop((left, top, right, bottom))
    return left_panel


def right_panel(mirrored, width, height, side_panel_width):
    left = 0
    top = 0
    right = side_panel_width
    bottom = height
    right_panel = mirrored.crop((left, top, right, bottom))
    return right_panel


def combine_images(left_panel, im, right_panel):
    images = [left_panel, im, right_panel]
    spw, sph = left_panel.size
    width, height = im.size
    max_height = height
    total_width = (2*spw) + width

    new_im = Image.new('RGB', (total_width, max_height))

    x_offset = 0
    for x in images:
        new_im.paste(x, (x_offset,0))
        x_offset += x.size[0]
    #new_im.show()
    return new_im

def paint_in(panel_width, im):
    width, height = im.size
    ori = cv2.cvtColor(np.array(im.convert('RGB')), cv2.COLOR_RGB2BGR)
    mask = np.zeros((height, width), np.uint8)
    cv2.rectangle(mask, (panel_width-12, height//4), (panel_width+12, 3*height//4), 255, -1)
    mask = cv2.blur(mask, (20, 20))
    mask[mask>128] = 255
    mask[mask<=128] = 0
    dst = cv2.inpaint(ori, mask, 3, cv2.INPAINT_TELEA)
    final = Image.fromarray(dst)
    return final

if __name__ == '__main__':
    im = Image.open('test.png')
    mi = flip_image(im)
    width, height = im.size
    panel_width = side_panel_width(im)
    l = left_panel(mi, width, panel_width)
    r = right_panel(mi, width, panel_width)

    combine_images(l, im, r)
    





