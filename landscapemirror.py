#Landscape: 10:8

import numpy as np
from PIL import Image
import sys
import math
 
if __name__ == '__main__':
    
# def output_image(im):
    im = Image.open('test.png')

    def flip_image(image):
        """
        Flip or mirror the image
        @param image_path: The path to the image to edit
        @param saved_location: Path to save the cropped image
        """
      
        mirror = image.transpose(Image.FLIP_TOP_BOTTOM)
        # mirror.save(saved_location)
        # mirror.show()
        return mirror
     
    # if __name__ == '__main__':
    #     image = 'mantis.png'
    #     flip_image(image, 'flipped_mantis.jpg')


    def panel_height(image):
        width, height = image.size
        targetheight = width*0.8
        #Horizontal Image
        panel_height = math.ceil((targetheight-height) /2)
        return panel_height



    def top_panel(mirrored, width, height, panel_height):
        left = 0
        top = height - panel_height
        right = width
        bottom = height
        top_panel = mirrored.crop((left, top, right, bottom))
        return top_panel


    def b_panel(mirrored, width, height, panel_height):
        left = 0
        top = 0
        right = width
        bottom = panel_height
        b_panel = mirrored.crop((left, top, right, bottom))
        return b_panel


    def combine_images(top_panel, im, b_panel):
        images = [top_panel, im, b_panel]
        pw, ph = top_panel.size
        width, height = im.size
        max_height = (2*ph) + height
        total_width = width

        new_im = Image.new('RGB', (total_width, max_height))

        x_offset = 0
        for x in images:
            new_im.paste(x, (0, x_offset))
            x_offset += x.size[1]

        # return new_im
        # new_im.show()
        new_im.save('mirror.png', 'PNG')

    mi = flip_image(im)
    width, height = im.size
    panel_height = panel_height(im)
    t = top_panel(mi, width, height, panel_height)
    b = b_panel(mi, width, height, panel_height)

    combine_images(t, im, b)
    




