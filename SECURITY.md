# Security Policy

## Supported use

This repository is a prototype and portfolio project. It should not be treated as production-hardened software.

## Reporting

If you discover a security-sensitive issue, please avoid posting secrets, tokens, or exploit details in a public issue.

Open a minimal issue describing the affected area and the general impact, or contact the maintainer through a private channel if one is available.

## Sensitive material to avoid committing

- model provider API keys
- search provider keys
- local machine paths
- generated credentials
- private datasets or downloaded artifacts

## Hardening gaps

Known areas that still need stronger hardening:

- tool execution safety and allow-listing
- external provider dependency isolation in tests
- clearer secret-management setup for local development
