# Module 1 Product Requirements — UM BorrowBuddy

Status: DRAFT FOR STUDENT AND TEAM REVIEW
Date: 2026-11-02
Course: WIA2007; PIC: Vincent
Scope: User Authentication & Profile only.
All cross-module contracts and product choices labelled PROPOSED need teammate approval.

## 1. Evidence and boundaries

Current local project: C:/Users/Vincent Loh/AndroidStudioProjects/UMBorrowBuddy; rootProject.name UMBorrowBuddy; one app (:app); namespace/application ID com.example.umborrowbuddy. Java application source and XML Views with AppCompat; Gradle Kotlin DSL; no Compose configuration or Firebase dependencies. LoginActivity is the local UI-only launcher. The team repository and backend remain unverified.
The initial assembleDebug invocation passed. See M1_LOGIN_STAGE.md for final-stage verification. Device, email delivery and backend security tests remain NOT RUN.

User-provided team repository: https://github.com/junonnnnn/um-borrow-buddy.
Its contents have not been inspected. Do not assume it matches this local folder.

## 2. Requirements and journeys

### FR1 — Authentication

Register with a valid UM email using Email/Password Firebase Authentication. Allowed domains are exactly siswa.um.edu.my and um.edu.my. Send an ownership verification email. Login and logout securely. A signed-in user is not automatically eligible: unverified and non-UM users must be denied protected features by both application routing and database rules.

Journey: Login → Register → verification pending → open email → return and check verification → create missing profile → shared application destination.
Returning user: launch → check session → Login, verification screen, profile creation, or shared application destination as appropriate.
A failed verification refresh must show retry feedback and must not grant access based on an assumed success.

### FR2 — Password reset

Login → Reset Password → validate UM email → request Firebase reset email → confirmation.
Show invalid input locally. Handle network failure and rate limiting; allow retry without duplicate submissions.
Use neutral success wording: “If an eligible account exists, a password reset email will be sent.” Do not promise that a specific account exists or that delivery is immediate.

### FR3 — Profile

Eligible authenticated users create, view, and edit display name, faculty, residential college, and preferred public meetup spot. Save to users/{uid}; load that document when they return.
A failed read must remain a read error, not be treated as a missing profile. Preserve entered values after save failure; display success only after the server acknowledges saving.
PROPOSED: require a complete profile before entering the shared application. Team must approve this gate.

## 3. Screens

| Screen | Main actions / states |
|---|---|
| Login | Email, password, login, register link, reset link; loading/error |
| Register | Email, password, confirmation; validation and request feedback |
| Verify Email | Instructions, resend, check verification, sign out; retry/rate-limit feedback |
| Reset Password | Email, send request, confirmation/error |
| Profile | Create/view/edit modes; four fields, save/cancel, loading/error |
| Shared application destination | Owned by integration lead; no Module 2 implementation in this scope |

## 4. Validation — PROPOSED until reviewed

- Trim surrounding email whitespace; compare the domain case-insensitively. Validate email structure and exact domain. Reject name@um.edu.my.example.com, empty local parts, and unrelated subdomains.
- Agree on email normalisation across registration, login, reset, and rules before implementation.
- Password confirmation must match. Do not trim passwords. Match the configured Firebase password policy; record that policy before coding rather than inventing a strength requirement.
- Display name: trimmed, 1–60 characters.
- Faculty and residential college: trimmed, 1–100 characters each. Team must choose free text versus approved lists and how off-campus users are represented.
- Preferred meetup spot: trimmed, 1–120 characters; public campus location, no residential room details required.
- Apply the same field/type/length constraints in Firestore rules.
- Do not store passwords, verification flags controlled by clients, or authentication tokens in profile documents.

## 5. Measurable acceptance criteria

| ID | Pass condition / required evidence |
|---|---|
| A1 | Each allowed domain can register with an accessible test inbox; malformed and lookalike domains are rejected before a request. |
| A2 | Registration sends verification; signed-in unverified user cannot enter protected screens or read/write protected Firestore data. |
| A3 | After real email verification, reload and ID-token refresh permit the correct next screen; failure offers retry. |
| A4 | Valid login restores the correct UID; wrong credentials show safe feedback; non-UM signed-in account is denied. |
| A5 | Logout clears protected navigation and displayed user state; Back and direct protected entry cannot restore access. |
| A6 | Reset validates input, handles offline/rate-limit failure, and shows neutral confirmation on success; real reset link permits subsequent login with the new password. |
| A7 | Create/save/relaunch loads the same four profile values under users/{current UID}. |
| A8 | Edit/save/relaunch loads updated values; failed save preserves input and never reports server success. |
| A9 | User B cannot read/write A's private profile; signed-out, unverified, and non-UM identities fail protected rules tests. |
| A10 | Rotation/recreation preserves non-secret form state; repeated taps do not create concurrent duplicate operations; password fields are masked. |
| A11 | Module 2 joint ownership/session tests in the integration contract pass before integration acceptance. |

Record each case as NOT RUN, PASS, FAIL, or BLOCKED with evidence. No case is currently claimed as passed.

## 6. Out of scope

Listings, photos, borrowing flows, messaging, search, notifications, ratings, admin features, social login, profile photos, changing account email, and account deletion are outside this assigned implementation. Do not build a replacement main app or alter teammates' screens.

## 7. Academic contribution and evidence

Follow the user's stated WIA2007 Level 2 limited AI assistance. Vincent manually implements and adapts each stage, explains the validation and Firebase calls, reviews all suggested changes, and performs meaningful tests. Confirm any lecturer-specific restrictions with the course materials.
Maintain an AI Usage Log: date, tool, prompt/purpose, suggestion received, accepted/rejected/adapted parts, student contribution, files affected, verification evidence, and reviewer.
Use test identities; redact email addresses and secrets in shared evidence. Never record passwords or tokens.
