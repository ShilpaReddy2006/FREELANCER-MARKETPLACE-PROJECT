# Freelancer Marketplace Frontend
React + Vite frontend matched to the Spring Boot API contracts supplied in the project conversation.

## Run
1. Extract this ZIP.
2. Open terminal in `freelancer-marketplace-frontend`.
3. Run `npm install`.
4. Run `npm run dev` and open the URL Vite prints.
5. Run `npm run build` to verify a production build.

Default backend: `http://localhost:8081/api`. Copy `.env.example` to `.env` and set `VITE_API_URL` if needed.

## API endpoints used
Auth: POST `/auth/register`, POST `/auth/login`, GET `/test`
Projects: GET/POST `/projects`, PATCH `/projects/{id}/complete`
Applications: POST `/projects/{id}/applications`, GET `/projects/{id}/applications`, GET `/projects/applications/my`, POST `/projects/{id}/applications/{applicationId}/accept`
Contracts: POST `/contracts`, GET `/contracts/my`, PUT `/contracts/{id}/status`
Milestones: POST/GET `/contracts/{id}/milestones`, PUT `/milestones/{id}/status`
Reviews: POST `/reviews/contracts/{id}`, GET `/reviews/my-given`, GET `/reviews/my-received`

## Important
- Add the exact Vite origin (5173 or 5174) to Spring Security CORS allowed origins. Add the deployed frontend origin before production.
- Set `VITE_API_URL` to the deployed backend URL ending in `/api` when deploying.
- Backend authorization is authoritative; the role shown by the frontend comes from the JWT claim for display only.
- The backend files supplied do not expose messaging, profile editing, or notification-list endpoints; these are not implemented as live features.
- Creating a contract currently asks for an accepted application ID. This matches the provided POST `/contracts` DTO (`applicationId`, `startDate`, `endDate`).
