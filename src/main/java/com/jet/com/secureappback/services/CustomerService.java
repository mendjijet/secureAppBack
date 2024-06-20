package com.jet.com.secureappback.services;

import com.jet.com.secureappback.domain.Customer;
import com.jet.com.secureappback.domain.Invoice;
import com.jet.com.secureappback.domain.Stats;
import org.springframework.data.domain.Page;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 19/06/2024
 */
public interface CustomerService {
    // Customer functions
    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomers(String name, int page, int size);

    // Invoice functions
    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long id, Invoice invoice);
    Invoice getInvoice(Long id);
    Stats getStats();
}
