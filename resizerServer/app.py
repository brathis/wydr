from flask import Flask, request, send_from_directory
import os, io
from PIL import Image

from resizer import imagemirror, landscapemirror, portraitmirror, squaremirrorp, squaremirrorl

app=Flask(__name__)
APP_ROOT = os.path.dirname(os.path.abspath(__file__))


@app.route('/')
def landing():
    return 'Hello World'

@app.route('/static/images/<filename>')
def send_file(filename):
    return send_from_directory("static/images", filename)


@app.route('/image_binary/painted/<targettype>', methods=["POST"])
def image_binary_painted(targettype):
    target = os.path.join(APP_ROOT, 'static/images/')

    #create directory if not found
    if not os.path.isdir(target):
        os.mkdir(target)

    #retrieve file from POST submission
    upload = request.data
    filename = "temp.png"

    # save file
    destination = "/".join([target, filename])
    print("File saved to: ", destination)

    # Open file for processing
    im = Image.open(io.BytesIO(upload))

    output = im

    # Process image
    width, height = im.size
    print(targettype)
    if targettype == "toportrait":
        print("Converting to Portrait")
        mi = imagemirror.flip_image(im)
        panel_width = imagemirror.side_panel_width(im)
        l = imagemirror.left_panel(mi, width, height, panel_width)
        r = imagemirror.right_panel(mi, width, height, panel_width)
        output = imagemirror.combine_images(l, im, r)
        output = imagemirror.paint_in(panel_width, output)
    elif targettype == "tolandscape":
        print("Converting to Landscape")
        mi = landscapemirror.flip_image(im)
        panel_height = landscapemirror.panel_height(im)
        t = landscapemirror.top_panel(mi, width, height, panel_height)
        b = landscapemirror.b_panel(mi, width, height, panel_height)
        output = landscapemirror.combine_images(t, im, b)
    elif targettype == "landscapetosquare":
        print("Converting Landscape to Square")
        mi = squaremirrorl.flip_image(im)
        panel_height = squaremirrorl.panel_height(im)
        t = squaremirrorl.top_panel(mi, width, height, panel_height)
        b = squaremirrorl.b_panel(mi, width, height, panel_height)
        output = squaremirrorl.combine_images(t, im, b)
    elif targettype == "portraittosquare":
        print("Converting Portrait to Square")
        mi = squaremirrorp.flip_image(im)
        panel_width = squaremirrorp.side_panel_width(im)
        l = squaremirrorp.left_panel(mi, width, height, panel_width)
        r = squaremirrorp.right_panel(mi, width, height, panel_width)
        output = squaremirrorp.combine_images(l, im, r)

    destination_mod = "/".join([target, "mod_"+filename])
    output.save(destination_mod)

    return send_file('mod_'+filename)


@app.route('/image_binary/<targettype>', methods=["POST"])
def image_binary(targettype):
    target = os.path.join(APP_ROOT, 'static/images/')

    #create directory if not found
    if not os.path.isdir(target):
        os.mkdir(target)

    #retrieve file from POST submission
    upload = request.data
    filename = "temp.png"

    # save file
    destination = "/".join([target, filename])
    print("File saved to: ", destination)

    # Open file for processing
    im = Image.open(io.BytesIO(upload))

    output = im

    # Process image
    width, height = im.size
    print(targettype)
    if targettype == "toportrait":
        print("Converting to Portrait")
        mi = imagemirror.flip_image(im)
        panel_width = imagemirror.side_panel_width(im)
        l = imagemirror.left_panel(mi, width, height, panel_width)
        r = imagemirror.right_panel(mi, width, height, panel_width)
        output = imagemirror.combine_images(l, im, r)
    elif targettype == "tolandscape":
        print("Converting to Landscape")
        mi = landscapemirror.flip_image(im)
        panel_height = landscapemirror.panel_height(im)
        t = landscapemirror.top_panel(mi, width, height, panel_height)
        b = landscapemirror.b_panel(mi, width, height, panel_height)
        output = landscapemirror.combine_images(t, im, b)
    elif targettype == "landscapetosquare":
        print("Converting Landscape to Square")
        mi = squaremirrorl.flip_image(im)
        panel_height = squaremirrorl.panel_height(im)
        t = squaremirrorl.top_panel(mi, width, height, panel_height)
        b = squaremirrorl.b_panel(mi, width, height, panel_height)
        output = squaremirrorl.combine_images(t, im, b)
    elif targettype == "portraittosquare":
        print("Converting Portrait to Square")
        mi = squaremirrorp.flip_image(im)
        panel_width = squaremirrorp.side_panel_width(im)
        l = squaremirrorp.left_panel(mi, width, height, panel_width)
        r = squaremirrorp.right_panel(mi, width, height, panel_width)
        output = squaremirrorp.combine_images(l, im, r)

    destination_mod = "/".join([target, "mod_"+filename])
    output.save(destination_mod)

    return send_file('mod_'+filename)

@app.route('/image_test', methods=["POST"])
def image_test():
    target = os.path.join(APP_ROOT, 'static/images/')

    #create directory if not found
    if not os.path.isdir(target):
        os.mkdir(target)

    #retrieve file from POST submission
    upload = request.files['image']
    print("File name: {}".format(upload.filename))
    filename = upload.filename

    # save file
    destination = "/".join([target, filename])
    print("File saved to: ", destination)
    upload.save(destination)

    # Open file for processing
    im = Image.open(upload)

    # Flip image
    mi = imagemirror.flip_image(im)
    width, height = im.size
    panel_width = imagemirror.side_panel_width(im)
    l = imagemirror.left_panel(mi, width, height, panel_width)
    r = imagemirror.right_panel(mi, width, height, panel_width)
    output = imagemirror.combine_images(l, im, r)

    destination_mod = "/".join([target, "mod_"+filename])
    output.save(destination_mod)

    return send_file('mod_'+filename)
