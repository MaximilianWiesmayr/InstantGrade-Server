from __future__ import print_function
import rawpy
import imageio
import ntpath
import os


def generateThumbnail(path):
    imagename = ntpath.basename(path).rsplit(".", 2)
    try:
        with rawpy.imread(path) as raw:
            thumb = raw.postprocess()
    except rawpy._rawpy.LibRawNonFatalError:
        thumb = imageio.imread(path)
    if not os.path.exists(ntpath.dirname(path) + "/thumbnail"):
        os.makedirs(ntpath.dirname(path) + "/thumbnail")
    savepath = ntpath.dirname(path) + "/thumbnail/" + imagename[0] + "_thumb.jpg"
    print(savepath)
    imageio.imsave(savepath, thumb)
