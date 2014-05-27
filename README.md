podval-imageio
==============

Extract (meta)data from image files.


Summary
-------

Flexible Java library for reading of the EXIF metadata, CRW (Canon raw) metadata and CRW images.


Motivation
----------


History
-------

Early 2002: development starts; some CRW information contributed to David Burren.

Middle of 2002: metadata description in XML introduced.

Early 2003: (pre-release) JAXB 1.0 used to generate classes for the reading of the metadata description from the W3C XML Schema

2005: reading of the metadata description redone using SAX

Early 2006: reader-to-application interface simplified


Related Work
------------

### Image and Metadata Extractors

* [Metadata Extractor](https://drewnoakes.com/code/exif/)
* [Java USB Camera Tools](http://jphoto.sourceforge.net/)
* [jhead](http://www.sentex.net/~mwandel/jhead/)
* [ExifTool](http://www.sno.phy.queensu.ca/~phil/exiftool/)
* [RawTherapee](http://www.rawtherapee.com/)


Future Work
-----------

* Reading more formats
* javax.imageio reader for CRW files
* javax.imageio metadata for CRW files
* javax.imageio metadata for JPEG files
* Writing of the EXIF data


References
----------

### Image File and Metadata Formats

* [EXIF 2.3](http://www.cipa.jp/std/documents/e/DC-008-2012_E.pdf)
* [CIFF](http://xyrion.org/ciff/CIFFspecV1R04.pdf)
* [Canon EXIF Maker Note](http://www.burren.cx/david/canon.html)
* [CRW Image (dcraw)](http://www.cybercom.net/~dcoffin/dcraw/)
* [DIG35](http://www.bgbm.org/tdwg/acc/Documents/DIG35-v1.1WD-010416.pdf)


### Demosaicing

* [Vector Color Filter Array Demosaicing](http://www-isl.stanford.edu/~abbas/group/papers_and_pub/spie01_gupta.pdf)
