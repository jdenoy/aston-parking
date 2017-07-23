'use strict';

/*
  Imports
 */
const gulp = require('gulp'),
    $ = require('gulp-load-plugins')({
        pattern: '*',
        rename:  {}
    });

/*
 Enabled isDev if --dev is available in command line
  */
const isDev = ($.util.env.dev !== undefined),
    isProd = ($.util.env.dev === undefined);

/* ------------------------- *\
    Configuration
\* ------------------------- */

/*
  Paths
 */
const path = {};

path.folder = {
    input:  'src',
    output: 'dist'
};

path.scss = {
    input:  path.folder.input + '/scss/*.scss',
    watch:  path.folder.input + '/scss/**/*.scss',
    output: path.folder.output + '/css'
};

path.html = {
    input:  path.folder.input + '/html/**/*',
    watch:  path.folder.input + '/html/**/*',
    output: path.folder.output
};

/* ------------------------- *\
    Common tasks
\* ------------------------- */

/*
  Default task
 */
gulp.task('default', ['build']);

/*
  Watch task
 */
gulp.task('watch', ['build', 'scss:watch', 'html:watch']);

/*
  Build
 */
gulp.task('build', ['clean'], function () {
    gulp.start(['scss', 'html']);
});

/*
  Clean task
 */
gulp.task('clean', function () {
    return $.del(path.folder.output);
});

/* ------------------------- *\
    STYLE tasks
\* ------------------------- */

/*
  SCSS task
 */
gulp.task('scss', function () {
    return gulp.src(path.scss.input)
        .pipe($.plumber().on('error', $.util.log))
        .pipe($.if(isDev, $.sourcemaps.init()))
        .pipe($.sass.sync())
        .pipe($.if(isProd, $.cssnano({discardComments: {removeAll: true}, safe: true})))
        .pipe($.autoprefixer())
        .pipe($.if(isDev, $.sourcemaps.write()))
        .pipe(gulp.dest(path.scss.output))
});

/*
  SCSS _watch_ task
 */
gulp.task('scss:watch', function () {
    gulp.watch(path.scss.watch, ['scss']);
});

/* ------------------------- *\
    HTML tasks
\* ------------------------- */

/*
  HTML task
 */
gulp.task('html', function () {
    return gulp.src(path.html.input)
        .pipe(gulp.dest(path.html.output))
});

/*
  HTML _watch_ task
 */
gulp.task('html:watch', function () {
    gulp.watch(path.html.watch, ['html']);
});
