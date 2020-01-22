from __future__ import print_function
import rawpy
import imageio
import ntpath


def createThumbnail(path):
    with rawpy.imread(path) as raw:
        rgb = raw.postprocess()
    imagename = ntpath.basename(path).rsplit(".", 2)
    savepath = ntpath.dirname(path) + "/" + imagename[0] + ".jpg"
    print(savepath)
    imageio.imsave(savepath, rgb)
