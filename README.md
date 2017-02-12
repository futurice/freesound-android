
# Futurice Freesound
[![Build Status](https://travis-ci.org/futurice/freesound-android.svg?branch=master)](https://travis-ci.org/futurice/freesound-android)
[![Sponsored](https://img.shields.io/badge/chilicorn-sponsored-brightgreen.svg)](http://spiceprogram.org/oss-sponsorship/)

Android client for the [Freesound Project](http://freesound.org/).

![Searching for Berlin sounds](https://github.com/futurice/freesound-android/raw/master/screenshots/screenshot1.png)

API is defined as per the [Freesound Project API documentation](http://freesound.org/docs/api/).

# Disclaimer

If you've stumbled across this repository - welcome!

This project is intended as a way for Futurice developers to hone their Android skills, learn from colleagues and experiment. We're only just getting started, so expect plenty of rough edges; we're not yet making any bold claims about using best practices that you should follow. Feel free to have a look at the code and be inspired or be appalled.

Thanks for your interest!

# Building

To build the app, you will need a Freesound API client id and secret stored in the file: `<repo>/app/freesound-api.properties`, as per:

```
freesound.api.clientId=yourapiclientidvaluegoeshere
freesound.api.clientSecret=yourapiclientsecretvaluegoeshere
```

If you don't have a key and you're not in the Futurice organization, then you will have to [generate your own from the Freesound website.](https://www.freesound.org/docs/api/overview.html)

Alternatively, you can [join us at Futurice](http://futurice.com/careers)!

# Contributing

If you're a Futuricean and feel like getting involved, then look at the list of [issues](https://github.com/futurice/freesound-android/issues) to find a task to work on. If you have an idea of your own, please add create a new issue and get the conversation started.

## Pull Requests

### Branching

Create a branch from the `master` branch with this format:

    git checkout -b <type>/<title>

Where `type` is one of the following:

* `feature` - a new feature, or part thereof.
* `fix` - a fix for a bug.
* `chore` - a technical or documentation task.

Example branch:
    
    chore/add-branching-model-to-readme

### Status

Use labels to show the status of your pull request:

* `in progress` - assigned by contributor: your PR is in progress. Do this as soon as you can.
* `awaiting review` - assigned by contributor: your PR is now ready for review. Ping some reviewers!
* `in review` - assigned by reviewer: your PR is being reviewed.
* `requires rework` - assigned by reviewer: you need to address the reviewer's feedback.
* `conditionally approved` -  assigned by reviewer: you may merge your pull request, subject to addressing the reviewer's feedback.
* `approved` -  assigned by reviewer: you may immediately merge your pull request. Hooray!

## IDE Configuration

### Android Studio
To ensure you that your source files match the project style, import the settings in the `ide` directory. These include:

1. Code style formatting settings
2. Copyright header

## Naming conventions

### Observables/Flowables
In order to more clearly understand the nature of the `Observables` and `Flowables` created, we employ the following naming convention:

- `Observable<...> getValueStream()` - when subscribed, may or may not emit any value, but it will never complete. Still, it might emit an error
- `Observable<...> getValueOnceAndStream()` - when subscribed, this will emit a value as soon as possible, then may or may not emit any value. Also can emit an error

This naming convention only applies to `Observables` and `Flowables`, for `Single`/`Completable`/`Maybe`, apply normal naming convention without the suffixes.

# Acknowledgements

Brought to you by the power of the [Chilicorn](http://spiceprogram.org/chilicorn-history/) and the [Futurice Open Source Program](http://spiceprogram.org/).

![Chilicorn Logo](https://raw.githubusercontent.com/futurice/spiceprogram/gh-pages/assets/img/logo/chilicorn_no_text-256.png)

# Other Sound Repositories

- [BioAcoustica](http://bio.acousti.ca)
- [British Library - Sounds](http://sounds.bl.uk)

License
=======

    Copyright 2016 Futurice GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
