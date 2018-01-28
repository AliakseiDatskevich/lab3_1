package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Justyna on 28.01.2018.
 */
public class BookKeeperTest {

    private TaxPolicy taxPolicy;

    @Before
    public void setUp(){
        taxPolicy = mock(TaxPolicy.class);
        Tax tax = new Tax(new Money(0.22, Money.DEFAULT_CURRENCY), "VAT");
        when(taxPolicy.calculateTax(Mockito.<ProductType>anyObject(), Mockito.<Money>anyObject())).thenReturn(tax);
    }

    @Test
    public void hasOneInvoiceLine() {
        RequestItem requestItem = createRequestItem1();

        ClientData clientData = new ClientData(Id.generate(), "Jan Kowalski");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);
        invoiceRequest.add(requestItem);

        InvoiceFactory invoiceFactory = new InvoiceFactory();

        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        List<InvoiceLine> lines = invoice.getItems();
        assertEquals(lines.size(), 1);
    }

    @Test
    public void isCalculateTaxCalledTwice() {
        RequestItem requestItem1 = createRequestItem1();
        RequestItem requestItem2 = createRequestItem2();

        ClientData clientData = new ClientData(Id.generate(), "Jan Kowalski");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

        invoiceRequest.add(requestItem1);
        invoiceRequest.add(requestItem2);

        InvoiceFactory invoiceFactory = new InvoiceFactory();

        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(Mockito.<ProductType>anyObject(), Mockito.<Money>anyObject());
    }

    @Test
    public void hasNoInvoiceLines() {
        ClientData clientData = new ClientData(Id.generate(), "Jan Kowalski");
        InvoiceRequest invoiceRequest = new InvoiceRequest(clientData);

        InvoiceFactory invoiceFactory = new InvoiceFactory();

        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        List<InvoiceLine> lines = invoice.getItems();
        assertEquals(lines.size(), 0);
    }

    private RequestItem createRequestItem1() {
        Money price = new Money(213.67, Money.DEFAULT_CURRENCY);
        ProductData productData = new ProductData(Id.generate(), price, "ticket", ProductType.STANDARD, new Date());
        return new RequestItem(productData, 2, price.multiplyBy(2));
    }

    private RequestItem createRequestItem2() {
        Money price = new Money(3.20, Money.DEFAULT_CURRENCY);
        ProductData productData = new ProductData(Id.generate(), price, "butter", ProductType.FOOD, new Date());
        return new RequestItem(productData, 3, price.multiplyBy(3));
    }
}