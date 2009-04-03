package org.podval.imageio;

import javax.imageio.stream.ImageInputStream;


public interface Walker {

    void readPrologue(ImageInputStream in);
}
