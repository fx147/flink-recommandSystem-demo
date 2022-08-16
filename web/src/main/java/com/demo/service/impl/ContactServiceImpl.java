package com.demo.service.impl;

import com.demo.dao.ContactDao;
import com.demo.domain.ContactEntity;
import com.demo.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("contactService")
public class ContactServiceImpl implements ContactService {

    @Autowired
    ContactDao contactDao;

    @Override
    public List<ContactEntity> selectByIds(List<String> ids) {
        List<ContactEntity> res = new ArrayList<>();

        for(String id : ids){
            ContactEntity contactEntity = selectById(id);
            res.add(contactEntity);
        }
        return res;
    }

    @Override
    public ContactEntity selectById(String id) {
        return contactDao.selectById(Integer.parseInt(id));
    }
}
