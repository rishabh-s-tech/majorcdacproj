# Changes in this revision

Fixes applied on top of the original GreenNest codebase, grouped by area.

## Security (backend)

- **Removed public admin self-registration.** `POST /api/users/register/admin` used to be
  reachable by anyone; it's now restricted to an already-authenticated admin
  (`SecurityConfig`). A single default admin is instead seeded on first startup
  (`config/AdminSeeder.java`), controlled via `APP_ADMIN_EMAIL` / `APP_ADMIN_PASSWORD`.
- **Fixed IDOR on cart and orders.** Every cart/order endpoint used to trust a client-supplied
  `userId`, so any logged-in user could read or modify another user's cart or orders by
  guessing an ID. All of these now resolve the acting user from the JWT via
  `security/CurrentUserProvider.java` instead:
  - `POST /api/cart/add`, `GET /api/cart`, `PUT /api/cart/{cartId}`, `DELETE /api/cart/{cartId}`
  - `POST /api/orders/place`, `GET /api/orders/me`
  - Cart/order mutations also check the item actually belongs to the caller (or that the
    caller is an admin) before allowing changes.
- **Closed a mass-assignment hole.** Registration used to bind directly onto the `User`
  JPA entity, so a client could send a `userId` and potentially overwrite another account,
  or set arbitrary fields. Registration now goes through a dedicated `RegisterRequest` DTO.
- **JWT secret externalized.** It was a hardcoded string in `JwtUtil.java`; it now comes from
  `JWT_SECRET` (falls back to a clearly-labeled dev-only default). Token expiry is also
  configurable via `JWT_EXPIRATION_MS`.
- **Password hash no longer leaks.** `User.password` is now write-only for JSON
  serialization, and registration responses use a `UserResponse` DTO that never carries it.
- **JwtFilter no longer 500s on a bad token.** Malformed/expired/garbage bearer tokens used
  to throw an uncaught exception; they're now treated as "not authenticated" and the request
  proceeds through normal authorization checks (which reject it with a clean 401/403).
- **Consistent error responses.** Added `exception/GlobalExceptionHandler.java` so
  not-found / bad-request / forbidden / unauthorized / validation failures all return a
  small JSON body (`{status, message, timestamp}`) instead of a raw stack trace or a bare
  500. Request bodies are validated with Bean Validation (`@Valid`).
- `AuthController` / `AuthService` were dead code duplicating `/api/users/login`. The sandbox
  used to produce this codebase wouldn't let me delete files after they're written, so they're
  still present but now unreachable (no route is permitted for them) - safe to delete manually.

## Correctness / business logic (backend)

- **Stock is now enforced.** Adding to cart or updating quantity is rejected if it would
  exceed a plant's `stockQuantity`. Placing an order re-validates stock for every line item
  and decrements it; the whole operation is wrapped in `@Transactional`.
- **Order status is validated** against a fixed set (`PLACED/PACKED/SHIPPED/DELIVERED/CANCELLED`)
  instead of accepting an arbitrary string.
- Added `GET /api/orders/{orderId}` for fetching a single order with the same
  ownership-or-admin check as the rest of the order endpoints.

## Frontend

- Added `AuthContext` and `CartContext` so login state and cart item count are shared
  application-wide, instead of every page reading `localStorage` directly.
- Added `ProtectedRoute` and wired it into `/cart`, `/orders`, `/admin/plants`, and
  `/admin/orders` in `App.jsx`, replacing per-page ad-hoc redirect checks.
- Added a response interceptor: any `401` from the API clears the stored session and
  bounces to `/login`, so an expired token doesn't leave the UI in a broken half-logged-in
  state.
- Cart/order calls no longer send a `userId` - they call the new `/api/cart`, `/api/cart/add`,
  `/api/orders/place`, `/api/orders/me` endpoints, matching the backend's identity-from-token
  model.
- Added a live cart-count badge on the Navbar's Cart link.
- Surfaced real backend error messages (e.g. "email already exists", "only 3 units in
  stock") in the login/register/cart/plant forms instead of generic fallback text.
- Fixed two ESLint findings introduced by the context additions (a fast-refresh export
  warning and a synchronous-setState-in-effect warning) so `npm run lint` is clean.

## What I could not verify here

This sandbox blocks Maven Central / Adoptium downloads and has no local JDK 17 or Maven
binary, so I could not run `./mvnw compile` or the existing test suite. I did a careful
manual read-through of every changed file (imports, braces, constructor wiring) and I'm
confident in the logic, but please run `./mvnw clean verify` before deploying.
The frontend was fully installed, linted, and built successfully in this environment.

## Catalog expansion (this revision)

- Added `config/PlantCatalogSeeder.java`: seeds 7 categories (Indoor Plants, Succulents &
  Cacti, Flowering Plants, Outdoor & Garden, Bonsai & Ornamental, Air-Purifying Plants,
  Herbs & Edibles) and 32 plants total on startup. It's idempotent - matched by name, so it
  only inserts what's missing and never duplicates or touches anything you've already added
  or edited through the admin UI.
- Added 27 new product thumbnail images (`front/greennest-frontend-new/public/images/`)
  to go with the new plants - stylized illustrated pot/plant icons generated to match the
  site's clean look, since real product photography wasn't available to source here. The
  original 5 photographic images (aloe-vera, bonsai, jade, money, succulent) are untouched.
  If you'd rather use real photography, just replace the files in `public/images/` with the
  same filenames referenced in `PlantCatalogSeeder.java` and rebuild.
- Frontend rebuilt (`npm run build`) and relinted (`npm run lint`) with the new images
  included - both pass clean.
