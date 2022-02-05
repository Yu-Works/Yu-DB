package com.icecreamqaq.test.db.dao;

import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(name = "test", uniqueConstraints = {@UniqueConstraint(name = "qq", columnNames = {"qq", "group"})})
public class TestEntity {
}
