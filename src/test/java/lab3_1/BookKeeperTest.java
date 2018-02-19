package lab3_1;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.hamcrest.Matchers;

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

public class BookKeeperTest {

    @org.junit.Test
    public void oneItemInvoiceTest() {
        Id id = new Id("1");
        Money money = new Money(1234);
        ProductData productData = new ProductData(id, money, "Item", ProductType.FOOD, new Date());
        RequestItem requestItem = new RequestItem(productData, 10, money);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = mock(InvoiceRequest.class);
        when(invoiceRequest.getItems()).thenReturn(Collections.singletonList(requestItem));
        when(taxPolicy.calculateTax(ProductType.FOOD, requestItem.getTotalCost()))
                .thenReturn(new Tax(new Money(0.25), "Item Tax"));
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getItems().size(), Matchers.is(1));
    }

    @org.junit.Test
    public void correctnessClientNameTest() {
        Id id = new Id("2");
        Money money = new Money(351);
        ProductData productData = new ProductData(id, money, "Item", ProductType.FOOD, new Date());
        RequestItem requestItem = new RequestItem(productData, 43, money);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
        Tax tax = new Tax(new Money(0.1), "Item Tax");
        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = mock(InvoiceRequest.class);
        ClientData clientData = new ClientData(id, "Aliaksei");
        when(invoiceRequest.getClientData()).thenReturn(clientData);
        when(taxPolicy.calculateTax(ProductType.FOOD, requestItem.getTotalCost())).thenReturn(tax);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertThat(invoice.getClient().getName(), Matchers.is(clientData.getName()));
    }
    
    @org.junit.Test
    public void twoItemInvoiceTest() {
        Id id = new Id("3");
        Money money = new Money(100);
        ProductData productData = new ProductData(id, money, "Item", ProductType.DRUG, new Date());
        RequestItem requestItem = new RequestItem(productData, 10, money);
        Tax tax = new Tax(new Money(0.25), "Item Tax");
        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        InvoiceRequest invoiceRequest = mock(InvoiceRequest.class);
        when(invoiceRequest.getItems()).thenReturn(Arrays.asList(requestItem, requestItem));
        when(taxPolicy.calculateTax(ProductType.DRUG, requestItem.getTotalCost())).thenReturn(tax);
        verify(taxPolicy, times(2)).calculateTax(ProductType.DRUG, requestItem.getTotalCost());
    }
    
    @org.junit.Test
    public void checkTimesOfInvoiceFactoryUsed() {
        Id id = new Id("4");
        ClientData clientData = new ClientData(id, "Dmitry");
        InvoiceFactory invoiceFactory = mock(InvoiceFactory.class);
        InvoiceRequest invoiceRequest = mock(InvoiceRequest.class);
        when(invoiceRequest.getClientData()).thenReturn(clientData);
        verify(invoiceFactory, times(1)).create(clientData);
    }
}
