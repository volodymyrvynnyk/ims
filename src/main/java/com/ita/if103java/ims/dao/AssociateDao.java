package com.ita.if103java.ims.dao;

import com.ita.if103java.ims.entity.Associate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ita.if103java.ims.entity.AssociateType;

import java.util.List;

public interface AssociateDao {

    Associate create(Long accountId, Associate associate);

    Associate findById(Long accountId, Long id);

    List<Associate> findByAccountId(Long accountId);

    Page<Associate> getAssociates(Pageable pageable, long accountId);

    Associate update(Long accountId, Associate associate);

    boolean delete(Long accountId, Long id);

    List<Associate> getAssociatesByType(Long accountId,  AssociateType type);
}
