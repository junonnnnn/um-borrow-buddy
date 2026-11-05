# Module 1 ↔ Module 2 Integration Contract

Status: Cross-module contracts remain PROPOSED; local project facts below are verified.
Date: 2026-11-02
Module 1: Vincent — Authentication & Profile
Module 2: Jin Seng — Item Listing Management

Local starter inspected; GitHub repository content, Firebase backend and Module 2 implementation have not been verified. This document is an agreement proposal, not deployed security or implemented functionality.

## 1. One app and backend — PROPOSED

One shared Android app and repository: https://github.com/junonnnnn/um-borrow-buddy.
Confirmed local identity: com.example.umborrowbuddy in UMBorrowBuddy, Java/XML. Canonical team checkout and final shared identity remain PROPOSED. All modules share one Firebase project, Android registration, Auth system and Firestore target.
Record before coding: Firebase project ID, Android application ID, Firestore database ID/edition/location, setup owner and rules deployment owner. No credentials belong in this contract.

## 2. Identity — PROPOSED

FirebaseAuth current user's UID is the canonical user ID. It is a string; do not derive it from email or display name.
Get UID from the authenticated session when performing operations. Intent extras and editable form values are not proof of ownership.
Do not duplicate Firebase emailVerified as an editable profile field. Auth is authoritative for email, verification and passwords.
Module 2 must use the same FirebaseAuth instance/project and Module 1 eligibility contract.

## 3. Profile document and Java model — PROPOSED

Path: users/{uid}; document ID is the Auth UID. No duplicate uid field is required in the stored document.

| Field | Type | Constraint |
|---|---|---|
| displayName | string | Trimmed, 1–60 characters |
| faculty | string | Trimmed, 1–100; free text/list decision pending |
| residentialCollege | string | Trimmed, 1–100; off-campus representation pending |
| preferredMeetupSpot | string | Trimmed, 1–120; public location |
| createdAt | timestamp | Server timestamp on create; immutable thereafter |
| updatedAt | timestamp | Server timestamp on each accepted save |

Shared Java model: com.example.umborrowbuddy.shared.model.UserProfile, with a public no-argument constructor and consistent field accessors. Confirm timestamp mapping before implementation.
Keep display name authoritative in Firestore. Do not silently maintain conflicting Auth and Firestore display names.
On missing profile, route to creation; on read error, show retry. A complete profile gate is proposed, pending team agreement.
Save only approved fields; updates must not erase createdAt or unrelated future agreed fields.

## 4. Item ownership boundary — PROPOSED

Module 1 integration requirement: share Firebase Auth UID with Jin Seng through items/{itemId}.ownerUid, a string equal to the authenticated creator's UID. This is a required design boundary, not implemented Module 2 code; teammate acceptance and joint testing remain pending.
Jin Seng owns the Item Java model and listing schema. Titles, categories, conditions, lending terms, photos and visibility values require his explicit field/type agreement; this document does not invent a confirmed schema for them.
ownerUid is immutable after creation. A profile edit does not change item ownership.
Confirm whether displays resolve the latest user name by UID or use a listing snapshot. Snapshot names must never authorise writes.

## 5. Shared authentication/session interface — PROPOSED

AuthGuard returns one of SIGNED_OUT, UNVERIFIED, INELIGIBLE_DOMAIN, ELIGIBLE, or CHECK_FAILED.
- SIGNED_OUT → Login.
- UNVERIFIED → Verify Email.
- INELIGIBLE_DOMAIN → deny protected access and offer sign out.
- CHECK_FAILED → retry; no newly granted protected access.
- ELIGIBLE → check required profile, then shared application destination.

Validate exact allowed domains siswa.um.edu.my and um.edu.my consistently across client and rules.
After email verification, reload FirebaseUser and refresh the ID token before protected requests. Handle each failure explicitly.
Use lifecycle-aware screen entry/resume checks plus Auth state observation. Auth state observation alone does not establish a newly changed email-verification state.
Attach/remove user-specific listeners with lifecycle/session changes. Reject late results from a previous UID and clear displayed private state on account change.
Firestore rules remain authoritative even if an Activity is opened directly.

## 6. Navigation — PROPOSED

Activity-based explicit navigation is the simplest initial option; team must approve it against the actual team app.
Module 1 login/verification success returns to the shared shell through one agreed navigation entry, subject to profile completion.
The shared main destination's class name and owner are TBD; do not build a substitute main screen.
No UID/password/token Intent extras are needed for ordinary navigation.
Register/reset/verification screens have defined Back behaviour; logout clears protected history.
Record the final class/interface name, Intent/result keys if needed, and whether a safe pending destination survives login. Do not accept arbitrary external destinations.

## 7. Profile visibility — PROPOSED

Default users/{uid} is private: only its eligible owner can read/write.
Jin Seng must confirm which profile fields, if any, borrowers need. Do not make the whole profile readable to satisfy a name display.
Firestore document reads return a whole document; rules cannot redact individual fields. If public profile data is approved, jointly design a separate minimal public document and its update policy before implementation.
No public profile collection is confirmed or created by this proposal.

## 8. Rules responsibilities — PROPOSED policy, not executable rules

All protected requests require a non-null Auth identity, verified email claim, and exact allowed UM domain. Never rely on a client-supplied email or verification flag.
- users: caller UID equals document ID; validate allowed keys/types/lengths, required fields and server timestamps; createdAt immutable. Profile deletion is outside current scope.
- items create: new ownerUid equals caller UID; validate Jin Seng's agreed schema.
- items update: existing ownerUid equals caller UID AND new ownerUid equals existing ownerUid.
- items delete: existing ownerUid equals caller UID.
- items read/list: agree on visible/hidden item policy; owner may need access to their hidden listings. Query constraints must match rules; rules are not result filters.
- Everything else denied unless another reviewed module policy explicitly grants it.

One rules owner consolidates all six modules' policies. Firebase Storage photo rules are a separate Jin Seng/shared-owner responsibility and must enforce the agreed ownership path; Firestore rules do not secure image files.
Do not deploy from this document; database edition/target and final rules syntax must first be verified.

## 9. File ownership — PROPOSED

| Area | Responsible person / coordination |
|---|---|
| auth/, profile/, m1_ resources, Module 1 tests/docs | Vincent |
| items/, item resources/model/tests | Jin Seng |
| shared/model/UserProfile.java, AuthGuard contract | Vincent proposes; consumers review changes |
| MainActivity/launcher, shared shell/navigation | Integration lead TBD; coordinate both members |
| Manifest, Gradle scripts/catalog, app theme | Designated shared-file reviewer TBD |
| Firebase registration/config, rules/indexes | Setup/rules owners TBD; joint review |
| Other modules | Respective teammates; no changes by Module 1 without agreement |

Use module-specific Java packages and resource prefixes. Java packages do not isolate resource names.
Feature branches and small PRs; shared-file edits scheduled and reviewed. No Git mutations are authorised by this document.

## 10. Joint integration tests — PROPOSED acceptance gate

All cases initially NOT RUN. Vincent verifies auth/profile; Jin Seng operates listing features; both retain evidence.

| ID | Scenario | Expected result |
|---|---|---|
| I1 | A logs in as verified UM user | Shared shell opens after agreed profile gate; both modules see the same UID |
| I2 | A creates listing through Module 2 | items/{id}.ownerUid equals A's UID |
| I3 | A edits/hides/deletes own listing | Allowed according to agreed listing schema |
| I4 | B attempts write/delete on A's listing, including direct SDK requests | Denied by rules; UI hiding is insufficient evidence |
| I5 | A tries changing ownerUid to B | Denied |
| I6 | Signed-out, unverified UM and verified non-UM users attempt protected profile/item reads/writes | Denied |
| I7 | A edits display name/profile then reopens listing | Ownership unchanged; display follows agreed name policy |
| I8 | Logout, Back, direct Activity entry and account switch to B | No protected access or A's private rendered data/listeners |
| I9 | Return after verification and token refresh | Protected request succeeds for eligible user; failed refresh offers retry |
| I10 | Close/relaunch and profile read/save network failure | Correct profile loads; failures do not bypass gates or falsely report save success |
| I11 | B requests A's private profile | Denied; approved public-display design tested separately if adopted |
| I12 | Non-owner queries hidden items | Result/access follows agreed visibility rules and query design |

Record test ID, build/branch, device/API, emulator/live target, identities by pseudonym, expected/actual, status and evidence location. Never include passwords/tokens.

## 11. Approval checklist and academic review

For each agreement record decision, reviewer, date and rationale:
- [ ] Canonical team checkout, app identity and Java/XML versus mixed UI stack.
- [ ] Firebase target, password policy and setup/rules owners.
- [ ] Profile field constraints, faculty/college choices, profile completion gate and visibility.
- [ ] ownerUid contract, item schema/visibility and photo security.
- [ ] Shared guard, navigation destination and session/cache behaviour.
- [ ] Shared-file reviewers and joint tests.

Vincent manually implements reviewed Module 1 stages, explains decisions, runs meaningful tests and keeps an AI Usage Log. Jin Seng implements Module 2. No approval is implied by an unchecked proposal.
