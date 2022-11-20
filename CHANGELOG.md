# Change Log

All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

### Added

- Start a jetty server
- Middleware for common response patterns, e.g. returning a string from a request handler assumes status 200 and content-type "text/html"
- Test client helper
- Bind current request to liana.core/_request_
- Pre-configured Reitit router

[unreleased]: https://github.com/liana/liana/compare/0.0.1...HEAD
