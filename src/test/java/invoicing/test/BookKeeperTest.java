package invoicing.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mockito;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.invoicing.BookKeeper;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceFactory;
import pl.com.bottega.ecommerce.sales.domain.invoicing.InvoiceRequest;
import pl.com.bottega.ecommerce.sales.domain.invoicing.RequestItem;
import pl.com.bottega.ecommerce.sales.domain.invoicing.Tax;
import pl.com.bottega.ecommerce.sales.domain.invoicing.TaxPolicy;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class BookKeeperTest {

    @Test
    public void requestForInvoiceWithOnePosionReturnsInvoiceWithOnePosition() {
        Money money = new Money(1);
        Id id = new Id("1");
        String productName = "POTATO";
        Date date = new Date();
        int quantity = 1;
        String description = "VEGETABLE";
        InvoiceFactory invoiceFactory = new InvoiceFactory();
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        ProductData productData = new ProductData(id, money, productName, ProductType.STANDARD, date);
        RequestItem requestItem = new RequestItem(productData, quantity, money);
        InvoiceRequest invoiceRequestMock = mock(InvoiceRequest.class);
        int expectedResult = 1;
        TaxPolicy taxPolicyMock = mock(TaxPolicy.class);
        ArrayList<RequestItem> requestItemList = new ArrayList<>();
        requestItemList.add(requestItem);

        when(invoiceRequestMock.getItems()).thenReturn(requestItemList);
        when(taxPolicyMock.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));
        int actualResult = bookKeeper.issuance(invoiceRequestMock, taxPolicyMock).getItems().size();

        assertThat(actualResult, is(expectedResult));
    }

    @Test
    public void requestForInvoiceWithTwoPosionsShouldCallCalculateTaxTwoTimes() {
        Money money = new Money(1);
        Id id = new Id("1");
        String productName = "POTATO";
        Date date = new Date();
        int quantity = 1;
        String description = "VEGETABLE";
        InvoiceFactory invoiceFactory = new InvoiceFactory();
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        ProductData productData = new ProductData(id, money, productName, ProductType.STANDARD, date);
        RequestItem requestItem = new RequestItem(productData, quantity, money);
        InvoiceRequest invoiceRequestMock = mock(InvoiceRequest.class);
        TaxPolicy taxPolicyMock = mock(TaxPolicy.class);
        ArrayList<RequestItem> requestItemList = new ArrayList<>();
        requestItemList.add(requestItem);
        requestItemList.add(requestItem);

        when(invoiceRequestMock.getItems()).thenReturn(requestItemList);
        when(taxPolicyMock.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));
        bookKeeper.issuance(invoiceRequestMock, taxPolicyMock);

        verify(taxPolicyMock, Mockito.times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void checkIfInvoiceContainsCorrectTaxDescription() {
        Money money = new Money(1);
        Id id = new Id("1");
        String productName = "POTATO";
        Date date = new Date();
        int quantity = 1;
        String description = "VEGETABLE";
        InvoiceFactory invoiceFactory = new InvoiceFactory();
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        ProductData productData = new ProductData(id, money, productName, ProductType.STANDARD, date);
        RequestItem requestItem = new RequestItem(productData, quantity, money);
        InvoiceRequest invoiceRequestMock = mock(InvoiceRequest.class);
        TaxPolicy taxPolicyMock = mock(TaxPolicy.class);
        ArrayList<RequestItem> requestItemList = new ArrayList<>();
        requestItemList.add(requestItem);

        when(invoiceRequestMock.getItems()).thenReturn(requestItemList);
        when(taxPolicyMock.calculateTax(any(ProductType.class), any(Money.class)))
                .thenReturn(new Tax(money, description));
        String actualResult = bookKeeper.issuance(invoiceRequestMock, taxPolicyMock).getItems().get(0).getTax()
                .getDescription();

        assertThat(actualResult, is(description));
    }

}
