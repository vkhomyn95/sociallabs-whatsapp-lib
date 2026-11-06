# SocialLabs fork of Cobalt

The SocialLabs backend uses this fork of [Auties00/Cobalt](https://github.com/Auties00/Cobalt) for WhatsApp
(see ADR-0016 and ADR-0017 in the backend repository). It is published to GitHub Packages as
`com.github.auties00:cobalt-lib:0.1.0-sl.N`, because upstream has no release on Maven Central.

## Branches
- `master` follows upstream and has no changes of its own.
- `feat/*` hold one change each, on top of upstream `master`, so each can be offered upstream as a PR.
- `sociallabs` is `master` plus every `feat/*` branch, the `-sl.N` version and the publishing setup. It is what
  gets released.

## Changes on top of upstream
| Branch                   | Change                                                                                       |
|--------------------------|----------------------------------------------------------------------------------------------|
| `feat/webhook-override`  | Webhook callback overrides for the WABA and the phone number; `querySubscribedApps` reads the override where Graph reports it |
| `feat/cloud-context-filename` | Sending: `context.message_id` for a message quoting another (contextual reply), `document.filename` for a named document |
| `feat/cloud-template-send` | `sendTemplate` with Cloud's own template model (named, currency, date, header media/location, button payload/URL suffix/coupon/flow values); template examples sent as JSON, URL button examples, `parameter_format` |
| `feat/cloud-business-profile` | `queryCloudBusinessProfile`/`editCloudBusinessProfile` with `about`, `vertical`, `profile_picture_url`/`profile_picture_handle` |
| `feat/linked-companion-name` | Linked web client: `Options.Web.name` no longer throws `ClassCastException`; `Options.Web.companionName` sets the name shown under Linked Devices on the phone |

## Releasing
1. Merge or rebase the `feat/*` branches into `sociallabs`.
2. Raise the version: `sh ./mvnw versions:set -DnewVersion=0.1.0-sl.N -DprocessAllModules -DgenerateBackupPoms=false`.
3. Commit, then tag and push: `git tag v0.1.0-sl.N && git push origin sociallabs v0.1.0-sl.N`.
4. The `publish` workflow builds on JDK 25, runs the fork's tests and deploys every module to GitHub Packages.

## Updating from upstream
1. `git fetch upstream && git checkout master && git merge --ff-only upstream/master`.
2. Rebase each `feat/*` branch onto `master` and rebuild `sociallabs` from them.
3. Release a new `-sl.N`, then run the backend's WhatsApp adapter contract tests before bumping it there.

## Native libraries
The calls and media transcoding code loads `libcobalt-native` through FFM. The jar does not include it: on first
use it is downloaded from the upstream repository at the commit pinned in `META-INF/native-checksums.json`,
checked against its SHA-256, and cached under `~/.cobalt/cache/natives`, or under `-Dcobalt.natives.cache`.
