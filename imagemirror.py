

#Portrait: Vertical Image:  1080px in width by 1350px in height =8:10

import numpy as np
from PIL import Image
import sys
 
 
def flip_image(image, saved_location):
    """
    Flip or mirror the image
    @param image_path: The path to the image to edit
    @param saved_location: Path to save the cropped image
    """
  
    mirror = image.transpose(Image.FLIP_LEFT_RIGHT)
    mirror.save(saved_location)
    mirror.show()
 
# if __name__ == '__main__':
#     image = 'mantis.png'
#     flip_image(image, 'flipped_mantis.jpg')


def side_panel_dim(image):
    width, height = image.size
    targetwidth = height*0.8 
    #Vertical Image:  1080px in width by 1350px in height = 0.8 = 8:10
    side_panel_width = (targetwidth-width) /2
    return side_panel_width



def left_panel(mirrored, width):
    left = width-side_panel_width
    top = 0
    right = width
    bottom = height
    left_panel = original.crop((left, top, right, bottom))


def right_panel(mirrored, width):
    left = 0
    top = 0
    right = side_panel_width
    bottom = height
    left_panel = original.crop((left, top, right, bottom))


def combine_images(left_panel, im, right_panel):
    images = [left_panel, im, right_panel]
    width, height = image.size
    max_height = height
    total_width = height*0.8 

    new_im = Image.new('RGB', (total_width, max_height))

    x_offset = 0
    for im in images:
        new_im.paste(im, (x_offset,0))
        x_offset += im.size[0]

    new_im.save('test.jpg')
    
    





