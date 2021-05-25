from __future__ import print_function
from PIL import Image
import rawpy
import imageio
import ntpath
import os
import sys
import imghdr
import shutil

arg1 = sys.argv[1]
arg2 = sys.argv[2]
arg3 = sys.argv[3]


def generateThumbnail(path=arg2, empty=arg3):
    imagename = ntpath.basename(path).rsplit(".", 2)
    img = Image.open(path)

    if imagename[1] == "jpg" or imagename[1] == "tif" or imagename[1] == "tiff" or imagename[1] == "png":
        thumb = Image.open(path)
        thumb = thumb.convert('RGB')
    else:
        with rawpy.imread(path) as raw:
            thumb = raw.postprocess()
    if not os.path.exists(ntpath.dirname(path) + "/thumbnail"):
        os.makedirs(ntpath.dirname(path) + "/thumbnail")
    savepath = ntpath.dirname(path) + "/thumbnail/" + imagename[0] + "_thumb.jpg"

    imageio.imsave(savepath, thumb)

def delete(path=arg2, empty=arg3):
    imagename = ntpath.basename(path).rsplit(".", 2)
    deletepath = ntpath.dirname(path) + "/thumbnail/" + imagename[0] + "_thumb.jpg"
    deleteeditedpath = ntpath.dirname(path) + "/edited/" + imagename[0] + ".tiff"

    os.remove(path)
    os.remove(deletepath)
    if os.path.exists(deleteeditedpath):
        os.remove(deleteeditedpath)

def prepareDownload(path=arg2, type=arg3):
    imagename = ntpath.basename(path).rsplit(".", 2)

    if os.path.exists(ntpath.dirname(path) + "/forDownload"):
        shutil.rmtree(ntpath.dirname(path) + "/forDownload")
    if not os.path.exists(ntpath.dirname(path) + "/edited/" + imagename[0] + ".tiff"):
        if imghdr.what(path) == 'png' or 'jpeg' or 'tiff':
            img = Image.open(path)
            img = img.convert('RGB')
        else:
            with rawpy.imread(path) as raw:
                img = raw.postprocess()
    else:
        img = Image.open(ntpath.dirname(path) + "/edited/" + imagename[0] + ".tiff")
    os.makedirs(ntpath.dirname(path) + "/forDownload")
    imageio.imsave(ntpath.dirname(path) + "/forDownload/" + imagename[0] + "." + type, img)

def reset(path=arg2, empty=arg3):
    imagename = ntpath.basename(path).rsplit(".", 2)
    deleteeditedpath = ntpath.dirname(path) + "/edited/" + imagename[0] + ".tiff"

    if os.path.exists(deleteeditedpath):
        os.remove(deleteeditedpath)

if arg1 == "thumb":
    generateThumbnail()
elif arg1 == "delete":
    delete()
elif arg1 == "prepareDownload":
    prepareDownload()
elif arg1 == "reset":
    reset()
