package lab3_1;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
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

public class Zad3_1BookKeeperTest {

    private InvoiceFactory invoiceFactory;
    private TaxPolicy taxPolicy;
    private ProductData productData;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData;
    private BookKeeper bookKeeper;
    private RequestItem requestItem;
    private Money money;
    private Invoice invoice;
    private Id id;

    @Before
    public void setUp() {
        invoiceFactory = new InvoiceFactory();
        id = new Id("1234");
        String imie = "imie";
        clientData = new ClientData(id, imie);
        invoiceRequest = new InvoiceRequest(clientData);
        productData = mock(ProductData.class);
        taxPolicy = mock(TaxPolicy.class);
        money = new Money(200);
        requestItem = new RequestItem(productData, 2, money);
        bookKeeper = new BookKeeper(invoiceFactory);
    }

    @Test
    public void oneRequestShouldReturnInvoiceWithOneItemIncludedTest() {
        invoiceRequest.add(requestItem);

        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, "description"));

        invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().size(), is(1));
    }
}
