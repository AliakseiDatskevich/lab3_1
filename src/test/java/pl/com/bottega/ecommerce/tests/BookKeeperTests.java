package pl.com.bottega.ecommerce.tests;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Invoice;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceFactory;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItem;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Tax;
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxPolicy;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTests {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private TaxPolicy taxPolicy;
    private RequestItem requestItem;
    private ProductData productData;
    private Money money;
    private Invoice invoice;
    private Id id;
    private Tax tax;
    private ClientData clientData;

    @Before
    public void setUp() {
        id = new Id("123");
        money = new Money(100);
        productData = new ProductData(id, money, "Item", ProductType.FOOD, new Date());
        requestItem = new RequestItem(productData, 10, money);
        bookKeeper = new BookKeeper(new InvoiceFactory());
        tax = new Tax(new Money(0.25), "Item Tax");
        taxPolicy = mock(TaxPolicy.class);
        invoiceRequest = mock(InvoiceRequest.class);
        clientData = new ClientData(id, "Jan");
    }

    @Test
    public void issuanceInvoiceWithOneItem() {
        when(invoiceRequest.getItems()).thenReturn(Collections.singletonList(requestItem));
        when(taxPolicy.calculateTax(ProductType.FOOD, requestItem.getTotalCost()))
                .thenReturn(new Tax(new Money(0.25), "Item Tax"));
        invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), Matchers.is(1));
    }

    @Test
    public void issuanceInvoiceWithTwoItemsCallsCalculateTaxTwice() {
        when(invoiceRequest.getItems()).thenReturn(Arrays.asList(requestItem, requestItem));
        when(taxPolicy.calculateTax(ProductType.FOOD, requestItem.getTotalCost())).thenReturn(tax);
        invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(ProductType.FOOD, requestItem.getTotalCost());
    }

    @Test
    public void isClientNameCorrect() {
        when(invoiceRequest.getClientData()).thenReturn(clientData);
        when(taxPolicy.calculateTax(ProductType.FOOD, requestItem.getTotalCost())).thenReturn(tax);
        invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getClient().getName(), Matchers.is(clientData.getName()));
    }

    @Test
    public void isInvoiceFactoryMethodCreateCalledOnce() {
        InvoiceFactory invoiceFactory = mock(InvoiceFactory.class);
        when(invoiceRequest.getClientData()).thenReturn(clientData);
        invoice = new BookKeeper(invoiceFactory).issuance(invoiceRequest, taxPolicy);
        verify(invoiceFactory, times(1)).create(clientData);
    }
}
