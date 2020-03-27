package com.vaynenet.www.controller;

import java.util.Objects;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class EffectiveJava {
    public static final AtomicInteger counter = new AtomicInteger();
    public final class CaseInsensitiveString {
        private final String s;

        public CaseInsensitiveString(String s) {
            this.s = Objects.requireNonNull(s);
        }

        // Broken - violates symmetry!
        @Override
        public boolean equals(Object o) {
            if (o instanceof CaseInsensitiveString)
                return s.equalsIgnoreCase(
                        ((CaseInsensitiveString) o).s);
            if (o instanceof String)  // One-way interoperability!
                return s.equalsIgnoreCase((String) o);
            return false;
        }
    // Remainder omitted
    }

}
