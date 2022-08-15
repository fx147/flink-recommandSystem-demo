package com.demo.service.impl;

import com.demo.dao.ProductDao;
import com.demo.domain.ContactEntity;
import com.demo.domain.ProductEntity;
import com.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Override
    public ProductEntity selectById(String id) {
        return productDao.selectById(Integer.parseInt(id));
    }

    @Override
    public List<ContactEntity> selectByIds(List<String> ids) {
        return productDao.selectByIds(ids);
    }
}
