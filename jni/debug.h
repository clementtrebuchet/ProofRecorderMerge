#include "config.h"

#include <android/log.h>

#ifdef DEBUG
#include <stdio.h>
#define debug(s) __android_log_print(ANDROID_LOG_DEBUG,"mpg123", s )
#define debug1(s, a) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a)
#define debug2(s, a, b) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b)
#define debug3(s, a, b, c) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c)
#define debug4(s, a, b, c, d) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d)
#define debug5(s, a, b, c, d, e) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e)
#define debug6(s, a, b, c, d, e, f) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f)
#define debug7(s, a, b, c, d, e, f, g) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g)
#define debug8(s, a, b, c, d, e, f, g, h) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h)
#define debug9(s, a, b, c, d, e, f, g, h, i) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i)
#define debug10(s, a, b, c, d, e, f, g, h, i, j) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i, j)
#define debug11(s, a, b, c, d, e, f, g, h, i, j, k) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i, j, k)
#define debug12(s, a, b, c, d, e, f, g, h, i, j, k, l) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l)
#define debug13(s, a, b, c, d, e, f, g, h, i, j, k, l, m) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m)
#define debug14(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m, n)
#define debug15(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) __android_log_print(ANDROID_LOG_DEBUG, "mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
#else
#define debug(s) 
#define debug1(s, a) 
#define debug2(s, a, b) 
#define debug3(s, a, b, c) 
#define debug4(s, a, b, c, d) 
#define debug5(s, a, b, c, d, e) 
#define debug6(s, a, b, c, d, e, f) 
#define debug7(s, a, b, c, d, e, f, g) 
#define debug8(s, a, b, c, d, e, f, g, h) 
#define debug9(s, a, b, c, d, e, f, g, h, i) 
#define debug10(s, a, b, c, d, e, f, g, h, i, j) 
#define debug11(s, a, b, c, d, e, f, g, h, i, j, k) 
#define debug12(s, a, b, c, d, e, f, g, h, i, j, k, l) 
#define debug13(s, a, b, c, d, e, f, g, h, i, j, k, l, m) 
#define debug14(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) 
#define debug15(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) 
#endif

/* warning macros also here... */
#ifndef NO_WARNING
#define warning(s) __android_log_print(ANDROID_LOG_WARN,"mpg123", s )
#define warning1(s, a) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a)
#define warning2(s, a, b) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b)
#define warning3(s, a, b, c) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c)
#define warning4(s, a, b, c, d) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d)
#define warning5(s, a, b, c, d, e) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e)
#define warning6(s, a, b, c, d, e, f) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f)
#define warning7(s, a, b, c, d, e, f, g) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g)
#define warning8(s, a, b, c, d, e, f, g, h) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h)
#define warning9(s, a, b, c, d, e, f, g, h, i) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i)
#define warning10(s, a, b, c, d, e, f, g, h, i, j) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i, j)
#define warning11(s, a, b, c, d, e, f, g, h, i, j, k) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k)
#define warning12(s, a, b, c, d, e, f, g, h, i, j, k, l) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l)
#define warning13(s, a, b, c, d, e, f, g, h, i, j, k, l, m) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m)
#define warning14(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m, n)
#define warning15(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) __android_log_print(ANDROID_LOG_WARN,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
#else
#define warning(s) 
#define warning1(s, a) 
#define warning2(s, a, b) 
#define warning3(s, a, b, c) 
#define warning4(s, a, b, c, d) 
#define warning5(s, a, b, c, d, e) 
#define warning6(s, a, b, c, d, e, f) 
#define warning7(s, a, b, c, d, e, f, g) 
#define warning8(s, a, b, c, d, e, f, g, h) 
#define warning9(s, a, b, c, d, e, f, g, h, i) 
#define warning10(s, a, b, c, d, e, f, g, h, i, j) 
#define warning11(s, a, b, c, d, e, f, g, h, i, j, k) 
#define warning12(s, a, b, c, d, e, f, g, h, i, j, k, l) 
#define warning13(s, a, b, c, d, e, f, g, h, i, j, k, l, m) 
#define warning14(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) 
#define warning15(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) 
#endif

/* error macros also here... */
#ifndef NO_ERRORMSG
#define error(s) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s )
#define error1(s, a) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a)
#define error2(s, a, b) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b)
#define error3(s, a, b, c) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c)
#define error4(s, a, b, c, d) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d)
#define error5(s, a, b, c, d, e) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e)
#define error6(s, a, b, c, d, e, f) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f)
#define error7(s, a, b, c, d, e, f, g) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g)
#define error8(s, a, b, c, d, e, f, g, h) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h)
#define error9(s, a, b, c, d, e, f, g, h, i) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i)
#define error10(s, a, b, c, d, e, f, g, h, i, j) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i, j)
#define error11(s, a, b, c, d, e, f, g, h, i, j, k) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k)
#define error12(s, a, b, c, d, e, f, g, h, i, j, k, l) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l)
#define error13(s, a, b, c, d, e, f, g, h, i, j, k, l, m) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m)
#define error14(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m, n)
#define error15(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) __android_log_print(ANDROID_LOG_ERROR,"mpg123", s , a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
#else
#define error(s) 
#define error1(s, a) 
#define error2(s, a, b) 
#define error3(s, a, b, c) 
#define error4(s, a, b, c, d) 
#define error5(s, a, b, c, d, e) 
#define error6(s, a, b, c, d, e, f) 
#define error7(s, a, b, c, d, e, f, g) 
#define error8(s, a, b, c, d, e, f, g, h) 
#define error9(s, a, b, c, d, e, f, g, h, i) 
#define error10(s, a, b, c, d, e, f, g, h, i, j) 
#define error11(s, a, b, c, d, e, f, g, h, i, j, k) 
#define error12(s, a, b, c, d, e, f, g, h, i, j, k, l) 
#define error13(s, a, b, c, d, e, f, g, h, i, j, k, l, m) 
#define error14(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) 
#define error15(s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) 
#endif


/* ereturn macros also here... */
#ifndef NO_ERETURN
#define ereturn(rv, s) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s); return rv; }while(0)
#define ereturn1(rv, s, a) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a); return rv; }while(0)
#define ereturn2(rv, s, a, b) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b); return rv; }while(0)
#define ereturn3(rv, s, a, b, c) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c); return rv; }while(0)
#define ereturn4(rv, s, a, b, c, d) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d); return rv; }while(0)
#define ereturn5(rv, s, a, b, c, d, e) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e); return rv; }while(0)
#define ereturn6(rv, s, a, b, c, d, e, f) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f); return rv; }while(0)
#define ereturn7(rv, s, a, b, c, d, e, f, g) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g); return rv; }while(0)
#define ereturn8(rv, s, a, b, c, d, e, f, g, h) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h); return rv; }while(0)
#define ereturn9(rv, s, a, b, c, d, e, f, g, h, i) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i); return rv; }while(0)
#define ereturn10(rv, s, a, b, c, d, e, f, g, h, i, j) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i, j); return rv; }while(0)
#define ereturn11(rv, s, a, b, c, d, e, f, g, h, i, j, k) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i, j, k); return rv; }while(0)
#define ereturn12(rv, s, a, b, c, d, e, f, g, h, i, j, k, l) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i, j, k, l); return rv; }while(0)
#define ereturn13(rv, s, a, b, c, d, e, f, g, h, i, j, k, l, m) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i, j, k, l, m); return rv; }while(0)
#define ereturn14(rv, s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i, j, k, l, m, n); return rv; }while(0)
#define ereturn15(rv, s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) do{ __android_log_print(ANDROID_LOG_ERROR,"mpg123", "ereturn: " s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o); return rv; }while(0)
#else
#define ereturn(rv, s) return rv
#define ereturn1(rv, s, a) return rv
#define ereturn2(rv, s, a, b) return rv
#define ereturn3(rv, s, a, b, c) return rv
#define ereturn4(rv, s, a, b, c, d) return rv
#define ereturn5(rv, s, a, b, c, d, e) return rv
#define ereturn6(rv, s, a, b, c, d, e, f) return rv
#define ereturn7(rv, s, a, b, c, d, e, f, g) return rv
#define ereturn8(rv, s, a, b, c, d, e, f, g, h) return rv
#define ereturn9(rv, s, a, b, c, d, e, f, g, h, i) return rv
#define ereturn10(rv, s, a, b, c, d, e, f, g, h, i, j) return rv
#define ereturn11(rv, s, a, b, c, d, e, f, g, h, i, j, k) return rv
#define ereturn12(rv, s, a, b, c, d, e, f, g, h, i, j, k, l) return rv
#define ereturn13(rv, s, a, b, c, d, e, f, g, h, i, j, k, l, m) return rv
#define ereturn14(rv, s, a, b, c, d, e, f, g, h, i, j, k, l, m, n) return rv
#define ereturn15(rv, s, a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) return rv
#endif
