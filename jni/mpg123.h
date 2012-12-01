/* Since we are not actually running the whole configuration
 * build process, this fills the need for mpg123.h, which
 * would normally be built from mpg123.h.in
 */

#define MPG123_NO_CONFIGURE
#include "libmpg123/mpg123.h.in"
