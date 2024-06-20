package com.jet.com.secureappback.repository;

import com.jet.com.secureappback.domain.Invoice;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 19/06/2024
 */
public interface InvoiceRepository extends PagingAndSortingRepository<Invoice, Long>, ListCrudRepository<Invoice, Long> {}
