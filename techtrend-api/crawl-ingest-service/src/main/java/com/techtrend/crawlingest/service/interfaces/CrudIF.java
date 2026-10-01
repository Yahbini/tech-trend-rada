package com.techtrend.crawlingest.service.interfaces;

public interface CrudIF<E, D> {
  E create(D data);
}
