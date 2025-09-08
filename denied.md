Denied changes (intentionally not applied):

1. Collapse surefire + failsafe plugins into a single test phase (kept separate).
2. Create profile or adjustments to omit test dependencies for slimmer runtime artifact (left as-is; rely on -DskipTests when needed).
3. Add hard OpenGL 4.6 version assertion after context creation (no strict runtime guard added).
4. Refactor WindowContext fields (e.g., make handle volatile / additional destroy guard) beyond current simplification.

Rationale: Marked DENY by request; preserving existing behavior and flexibility for now.

