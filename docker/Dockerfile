FROM mcr.microsoft.com/mssql/server:2017-latest

ENV SA_PASSWORD=Password1234
ENV ACCEPT_EULA=Y

RUN locale-gen en_US.UTF-8 && update-locale
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

ADD ./entrypoint.sh /
ADD ./init.sh /
ADD ./db.sql /
ADD ./sp1_GetAccounts.sql /
ADD ./sp2_DeleteAccount.sql /
ADD ./sp3_DeleteProfile.sql /
ADD ./sp4_Login.sql /
ADD ./sp5_AddAccount.sql /
ADD ./sp6_AddProfile.sql /
ADD ./trigger_DeleteAccounts.sql /

RUN  chmod +x /entrypoint.sh
RUN  chmod +x /init.sh
RUN  chmod +x /db.sql
RUN  chmod +x /sp1_GetAccounts.sql
RUN  chmod +x /sp2_DeleteAccounts.sql
RUN  chmod +x /sp3_DeleteProfile.sql
RUN  chmod +x /sp4_Login.sql
RUN  chmod +x /sp5_AddAccount.sql
RUN  chmod +x /sp6_AddProfile.sql
RUN  chmod +x /trigger_DeleteAccounts.sql

EXPOSE 1433

ENTRYPOINT /bin/bash ./entrypoint.sh
