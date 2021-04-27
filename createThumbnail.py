from __future__ import print_function
from PIL import Image
import rawpy
import imageio
import ntpath
import os
import sys
import imghdr


arg1 = sys.argv[1]
arg2 = sys.argv[2]


def generateThumbnail(path=arg2):
    imagename = ntpath.basename(path).rsplit(".", 2)
    if imghdr.what(path) == 'png' or 'jpeg' or 'tiff':
        thumb = Image.open(path)
        thumb = thumb.convert('RGB')
    else:
        with rawpy.imread(path) as raw:
            thumb = raw.postprocess()
    if not os.path.exists(ntpath.dirname(path) + "/thumbnail"):
        os.makedirs(ntpath.dirname(path) + "/thumbnail")
    savepath = ntpath.dirname(path) + "/thumbnail/" + imagename[0] + "_thumb.jpg"
    print(savepath)
    imageio.imsave(savepath, thumb)

def delete(path=arg2):
    imagename = ntpath.basename(path).rsplit(".", 2)
    deletepath = ntpath.dirname(path) + "/thumbnail/" + imagename[0] + "_thumb.jpg"
    deleteeditedpath = ntpath.dirname(path) + "/edited/" + imagename[0] + ".tiff"
    print(deletepath)
    os.remove(path)
    os.remove(deletepath)
    os.remove(deleteeditedpath)


if arg1 == "thumb":
    generateThumbnail()
elif arg1 == "delete":
    delete()
