package ru.courier.office;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.courier.office.core.ApplicationStatus;
import ru.courier.office.core.Document;
import ru.courier.office.core.LocalSettings;
import ru.courier.office.core.Status;
import ru.courier.office.data.DataAccess;
import ru.courier.office.web.WebContext;

public class CourierOfficeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataAccess dataAccess = DataAccess.getInstance(getApplicationContext());
        WebContext webContext = WebContext.getInstance();
        List<ru.courier.office.core.Application> applications = mockApplications();
        for (ru.courier.office.core.Application application : applications) {
            dataAccess.addApplication(application);
        }
    }

    private List<ru.courier.office.core.Application> mockApplications() {
        List<ru.courier.office.core.Application> applications = new ArrayList<>();
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            // application1
            ru.courier.office.core.Application application1 = new ru.courier.office.core.Application();
            application1.ApplicationGuid = UUID.randomUUID().toString();
            application1.ApplicationStatus = ApplicationStatus.None;
            application1.MerchantGuid = UUID.randomUUID().toString();
            application1.MerchantName = "Юлмарт";
            application1.Inn = "510703445204";
            application1.Email = "info@ulmart.ru";
            application1.Site = "ulmart.ru";
            application1.ManagerName = "Юрий Владимирович Колесников";
            application1.ManagerPhone = "9257036221";
            application1.PersonGuid = UUID.randomUUID().toString();
            application1.PersonName = "Аркадий Арсеньев";
            application1.BirthDate = format.parse("11.09.1977 00:00:00");
            application1.Gender = 1;
            application1.Amount = "20345.67";
            application1.DeliveryAddress = "Ярославская область, г. Щедрино, ул. Ленина, дом 15, кв 34";
            application1.Created = format.parse("10.09.2017 17:00:00");

            application1.DocumentList = new ArrayList<>();
            Document document1 = new Document();
            document1.DocumentGuid = UUID.randomUUID().toString();
            document1.ApplicationGuid = application1.ApplicationGuid;
            document1.Title = "Скан паспорта";
            application1.DocumentList.add(document1);

            Document document11 = new Document();
            document11.DocumentGuid = UUID.randomUUID().toString();
            document11.ApplicationGuid = application1.ApplicationGuid;
            document11.Title = "Фото клиента";
            application1.DocumentList.add(document11);

            application1.StatusList = new ArrayList<>();
            Status status1 = new Status();
            status1.ApplicationGuid = application1.ApplicationGuid;
            status1.Code = "1";
            status1.Category = "2";
            status1.Info = "Заявка зарегистрирована";
            status1.Created = format.parse("11.09.2017 16:00:00");
            application1.StatusList.add(status1);

            Status status11 = new Status();
            status11.ApplicationGuid = application1.ApplicationGuid;
            status11.Code = "1";
            status11.Category = "2";
            status11.Info = "Предварительно одобрено";
            status11.Created = format.parse("11.09.2017 16:30:00");
            application1.StatusList.add(status11);

            Status status111 = new Status();
            status111.ApplicationGuid = application1.ApplicationGuid;
            status111.Code = "1";
            status111.Category = "2";
            status111.Info = "Оформление начато";
            status111.Created = format.parse("11.09.2017 17:15:00");
            application1.StatusList.add(status111);

            applications.add(application1);

            // application2
            ru.courier.office.core.Application application2 = new ru.courier.office.core.Application();
            application2.ApplicationGuid = UUID.randomUUID().toString();
            application2.ApplicationStatus = ApplicationStatus.None;
            application2.MerchantGuid = UUID.randomUUID().toString();
            application2.MerchantName = "Юлмарт";
            application2.Inn = "510703445204";
            application2.Email = "info@ulmart.ru";
            application2.Site = "ulmart.ru";
            application2.ManagerName = "Юрий Владимирович Колесников";
            application2.ManagerPhone = "9257036221";
            application2.PersonGuid = UUID.randomUUID().toString();
            application2.PersonName = "Ирина Прохорова";
            application2.BirthDate = format.parse("19.03.1981 00:00:00");
            application2.Gender = 2;
            application2.Amount = "20345.67";
            application2.DeliveryAddress = "Ярославская область, г. Щедрино, ул. Ленина, дом 15, кв 34";
            application2.Created = format.parse("21.10.2017 13:00:00");

            application2.DocumentList = new ArrayList<>();
            Document document2 = new Document();
            document2.DocumentGuid = UUID.randomUUID().toString();
            document2.ApplicationGuid = application2.ApplicationGuid;
            document2.Title = "Скан паспорта";
            application2.DocumentList.add(document2);

            Document document22 = new Document();
            document22.DocumentGuid = UUID.randomUUID().toString();
            document22.ApplicationGuid = application1.ApplicationGuid;
            document22.Title = "Фото клиента";
            application2.DocumentList.add(document22);

            application2.StatusList = new ArrayList<>();
            Status status2 = new Status();
            status2.ApplicationGuid = application2.ApplicationGuid;
            status2.Code = "1";
            status2.Category = "2";
            status2.Info = "Заявка зарегистрирована";
            status2.Created = format.parse("21.10.2017 13:30:00");
            application2.StatusList.add(status2);

            Status status22 = new Status();
            status22.ApplicationGuid = application2.ApplicationGuid;
            status22.Code = "1";
            status22.Category = "2";
            status22.Info = "Предварительно одобрено";
            status22.Created = format.parse("21.10.2017 15:05:43");
            application2.StatusList.add(status22);

            Status status222 = new Status();
            status222.ApplicationGuid = application2.ApplicationGuid;
            status222.Code = "1";
            status222.Category = "2";
            status222.Info = "Оформление начато";
            status222.Created = format.parse("21.10.2017 15:30:00");
            application2.StatusList.add(status222);

            applications.add(application2);

            // application3
            ru.courier.office.core.Application application3 = new ru.courier.office.core.Application();
            application3.ApplicationGuid = UUID.randomUUID().toString();
            application3.ApplicationStatus = ApplicationStatus.None;
            application3.MerchantGuid = UUID.randomUUID().toString();
            application3.MerchantName = "Юлмарт";
            application3.Inn = "510703445304";
            application3.Email = "info@ulmart.ru";
            application3.Site = "ulmart.ru";
            application3.ManagerName = "Юрий Владимирович Колесников";
            application3.ManagerPhone = "9357036331";
            application3.PersonGuid = UUID.randomUUID().toString();
            application3.PersonName = "Сергей Смирнов";
            application3.BirthDate = format.parse("21.05.1983 00:00:00");
            application3.Gender = 3;
            application3.Amount = "30345.67";
            application3.DeliveryAddress = "Ярославская область, г. Щедрино, ул. Ленина, дом 15, кв 34";
            application3.Created = format.parse("11.11.2017 13:00:00");

            application3.DocumentList = new ArrayList<>();
            Document document3 = new Document();
            document3.DocumentGuid = UUID.randomUUID().toString();
            document3.ApplicationGuid = application3.ApplicationGuid;
            document3.Title = "Скан паспорта";
            application3.DocumentList.add(document3);

            Document document33 = new Document();
            document33.DocumentGuid = UUID.randomUUID().toString();
            document33.ApplicationGuid = application1.ApplicationGuid;
            document33.Title = "Фото клиента";
            application3.DocumentList.add(document33);

            application3.StatusList = new ArrayList<>();
            Status status3 = new Status();
            status3.ApplicationGuid = application3.ApplicationGuid;
            status3.Code = "1";
            status3.Category = "3";
            status3.Info = "Заявка зарегистрирована";
            status3.Created = format.parse("11.11.2017 14:15:00");
            application3.StatusList.add(status3);

            Status status33 = new Status();
            status33.ApplicationGuid = application3.ApplicationGuid;
            status33.Code = "1";
            status33.Category = "3";
            status33.Info = "Предварительно одобрено";
            status33.Created = format.parse("11.11.2017 14:45:30");
            application3.StatusList.add(status33);

            Status status333 = new Status();
            status333.ApplicationGuid = application3.ApplicationGuid;
            status333.Code = "1";
            status333.Category = "3";
            status333.Info = "Оформление начато";
            status333.Created = format.parse("11.11.2017 15:15:34");
            application3.StatusList.add(status333);

            applications.add(application3);

        } catch (ParseException pex) {
            pex.printStackTrace();
        }
        return applications;
    }
}