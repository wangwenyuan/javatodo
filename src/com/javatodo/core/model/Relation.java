package com.javatodo.core.model;

public enum Relation {
    EQ("="),
    NEQ("!="),
    GT(">"),
    EGT(">="),
    LT("<"),
    ELT("<="),
    LIKE("like"),
    BETWEEN("between"),
    NOTBETWEEN("not between"),
    IN("in"),
    NOTIN("not in");

    private Relation(String string){}
}
