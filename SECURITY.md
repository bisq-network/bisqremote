# Security Policy

  ## Supported Versions

  This repository contains Bisq Remote, a lightweight companion application for
  Bisq notifications and remote control. It handles pairing, encrypted
  notification messages, relay communication, QR-code setup, and recipient/device
  management.

  Security fixes are applied to the active development branch and any deployed or
  distributed version currently supported by Bisq.

  | Version / Branch | Supported |
  | --- | --- |
  | `master` | :white_check_mark: |
  | Versions currently distributed or supported by Bisq | :white_check_mark: |
  | Active dependency-update or maintenance branches while under review | :white_check_mark: |
  | Old builds, unsupported forks, or locally modified builds | :x: |

  Users should only use builds and pairing data from trusted Bisq sources and
  should remove paired devices that are lost, compromised, or no longer used.

  ## Reporting a Vulnerability

  Please do **not** report security vulnerabilities through public GitHub issues,
  pull requests, Matrix rooms, forums, or social media.

  Report suspected vulnerabilities privately through GitHub's **Report a
  vulnerability** flow on this repository's Security page. If that option is not
  available, open a minimal public issue asking maintainers to enable a private
  security reporting channel, but do not include exploit details.

  Include as much detail as possible:

  - affected branch, commit, build, operating system, device type, or dependency;
  - affected component, such as pairing tokens, QR-code scanning, message
    encryption, relay communication, recipient management, APNS/Android recipient
    handling, notification payload parsing, image/webcam capture, or UI flows;
  - whether the issue affects pairing authorization, encryption keys, message
    confidentiality, message authenticity, remote actions, push-token handling,
    relay metadata, QR parsing, local storage, or device removal;
  - whether the issue can expose trade/dispute/offer metadata, notification
    contents, device identifiers, push tokens, pairing secrets, relay endpoints,
    local files, screenshots, webcam data, or user identity;
  - reproduction steps, logs with secrets redacted, malicious QR codes, malformed
    relay messages, notification payloads, screenshots, or proof of concept code
    where useful;
  - whether the issue depends on a malicious relay, malicious paired device,
    compromised phone or desktop, leaked pairing token, malformed QR image,
    dependency vulnerability, or network attacker.

  Bisq is an open-source project maintained by contributors. Response times may
  vary, but reports involving remote-control bypass, pairing-token compromise,
  message encryption or authentication failure, notification metadata leakage,
  push-token leakage, relay impersonation, or unauthorized device actions are
  treated as urgent security issues and will be triaged as quickly as possible.

  For lower-severity issues, maintainers will respond when contributor capacity is
  available.

  If the report is accepted, maintainers may coordinate a fix privately, prepare a
  patched build or dependency update, revoke or rotate affected credentials when
  needed, and publish an advisory after users have had a reasonable opportunity to
  update. If the report is declined, maintainers will explain the reason when
  possible.

  Please give maintainers reasonable time to investigate and release mitigations
  before public disclosure. For severe or actively exploited issues, coordinate
  timing with maintainers so public details do not increase risk to users.

  Bisq does not currently guarantee a bug bounty. Security work may be eligible
  for Bisq DAO compensation if it qualifies under the project's contributor and
  critical-bug processes.
