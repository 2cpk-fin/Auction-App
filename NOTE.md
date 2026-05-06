NOTE:
1) (orphanRemoval = true) means that if a child got deleted, instead some columns assign null values. The database deletes the whole row.
2) Cascade Type
a) PERSIST: When you save a parent, all new children in the list are automatically saved too
b) MERGE: When you update any changes to parent, all the children will be updated too
c) REMOVE: When you delete a parent, all the children will be deleted
d) DETACH: If the parent is detached from the JPA Session, all the children will be detached too
e) REFRESH: If the database changes, and you refresh the parent, all the children will be refreshed too for sync
f) ALL: Shortcut for all the types
3) Fetch Type
a) LAZY: Loads the parent first, and then the children later (if needed)
b) EAGER: Loads both the parent and children