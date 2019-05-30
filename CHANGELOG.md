# Change log


All updates that affect the released version of the Ascend Java Client will be detailed here. This project adheres to [Semantic Versioning](http://semver.org).

## [0.7.0-beta] - 2019-05-29
### Fixed 
- Participant's now initialize with the client instead of getting reused like before.
- Stack traces are now logged upon exceptions.
### Deprecated 
- Deprecating setAscendParticipant method in the AscendConfig builder in favor of passing the participant into the
AscendClient init method.
### Added
- Added audience filters, allowing for filtering of the participant through user attributes.
- AllocationStore now requires a unique user id to be given with the allocation to be stored.

## [0.6.0-beta] - 2019-04-16
### Added
- Deprecating the implementations AsyncHttpClientImpl and OkHttpClientImpl in favor of the implementations AsyncHttpClient and OkHttpClient. The new implementations provide more options when constructing an HttpClient.
