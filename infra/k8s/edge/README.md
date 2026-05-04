# Public `/api/**` Edge Reference

This directory contains the checked-in reference asset for the supported `2.0` public backend boundary.

## What it shows

- route only `/api/**` from the shared public host to `Service/technical-interview-demo`
- keep `/`, `/hello`, `/docs`, `/v3/api-docs*`, and `/actuator/**` off the public ingress for this application
- keep the one-public-origin assumption by attaching the `/api` rule to the same host used by the separate first-party UI

## What it does not show

- no vendor-specific controller annotations
- no WAF, challenge, or rate-limit implementation details
- no TLS or certificate automation
- no turnkey production package

## Deployment-Owned Controls

Add your real controller- or gateway-specific controls in deployment-owned overlays or platform policy:

- burst limiting plus suspicious-client challenge or block capability for `/api/session/oauth2/authorization/{registrationId}`
- per-client throttling, request-size enforcement, and rejection visibility for unsafe internet-public `/api/**` writes

## Files

- `public-api-ingress.yaml` is the small example ingress that exposes only `/api/**` from the app service
