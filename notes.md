# Параметры
### Основные понятия
+ 2 типа реализации IOC  контейнеров  
  + `BeanFactory` - базовый интерфейс.  
  Создает бин если явно вызвать метод getBean()  
  lazy (ленивая инициализация). 
  + `ApplicationContext` - расширенный интерфейс.  
  Сразу создаются все бины даже если их не используем,  
  Кушает больше ресурсов.
---  
### Область действия  
+ Область действия компонента `Singleton` и `Prototype`
+ Методы:
  + init-method
  + destroy-method
  + default-init-method
  + default-destroy-method  
    Данные методы вызываются перед созданием объекта   
    и перед их удалением GC.
+ Интерфейсы
  + `InitializingBean` и `DisposibleBean`  
  + `BeanPostProcessor`
+ Ленивая инициализация
  + Параметр lazy-init определяет создавать бин
### Bean scope
+ Singleton(Default)
  + возвращают единственный кэшированный экземпляр
  + ограничение на уровне контейнера, не на уровне класса
  + для определения в настройки бина XML прописать `scope="singleton"`
+ Prototype
  + новый экземпляр при каждом обращении
+ Контейнер управляет инициализацией,  
 но никто не запрещает создавать объекты вручную  
 Нежелательно это делать, лучше управление передавать контейнеру  
+ При использовании веб приложения `XmlWebApplicationContext` доступно еще 4 области 
  + request `@RequestScope` на уровне запроса
  + session `@SessionScope` на уровне сессии HTTP
  + application `@ApplicationScope` на уровне всего веб прилдожения, похож на область синглтон, но синглтон   
  определяется на уровне контекста приложения, которых может быть несколько.
  + websocket  
+ Так же можно содавать кастомные скоупы
### Особенности Prototype
+ В отличие от других областей, Spring не управляет полным жизненным циклом прототипа bean. Контейнер создает экземпляр,   
настраивает и иным образом собирает объект-прототип и передает его клиенту без дальнейшей записи этого  
экземпляра-прототипа. Destroy метод не вызывается.  
+ Для высвобождения памяти или ресурсов используется `BeanPostProcessor`
+ аналог оператора `new` в java
### Особенности Singleton
+ при внедрении в бин с областью синглтон бина с областью прототип, бин прототип создасться единожды.   
Даже при условии что мы будем вызывать синглтон бин несколько раз.
### Proxy scope
Если вы хотите внедрить (например) компонент с областью действия HTTP-запроса в другой компонент с более   
длительной областью действия, вы можете выбрать внедрение прокси-сервера AOP вместо компонента с областью действия.

Например, мы хотим внедрить бин со скоупом session в singleton:
```
 <bean id="userPreferences" class="com.something.UserPreferences" scope="session">
     <aop:scoped-proxy/>
 </bean>
 
 <bean id="userManager" class="com.something.UserManager">
     <property name="userPreferences" ref="userPreferences"/>
 </bean>
 ```
---
### Интерфейсы жизненного цикла  
+ Использование интерфейсов вместо явного прописывания в xml  
destroy и init методов  
  + Без дополнительных настроек       
  + Для любых бинов     
  + Явное использование spring API  
+ В XML  
  + Для каждого бина нужно отдельно прописывать
  + Нет явной связи с Spring API    
  
+ Дефолтная ленивая инициализация на контекст  
`default-lazy-init="true"`

### Интерфейсы `InitializingBean` и `DisposableBean` 
+ InitializingBean
  + Инициализация бина после того, как контейнер установил для бина все необходимые свойства.
  + Необходимо реализовать метод `void afterPropertiesSet() throws Exception;`
    ```  
    public class AnotherExampleBean implements InitializingBean {
      
          @Override
          public void afterPropertiesSet() {
              // do some initialization work
          }
      }
    ```
+ DisposableBean
  + Вызыввется метод `void destroy() throws Exception;` при уничтожении бина контейнером.
    ```  
    public class AnotherExampleBean implements DisposableBean {
      
          @Override
          public void destroy() {
              // do some destruction work (like releasing pooled connections)
          }
     }
    ```
  
+ Не рекомаендовано использовать, лучше в xml прописать init и destroy метод,  
  альтернатива - использовать @PostConstruct и @PreDestroy над методом инициализации.
### Дефолтные методы инициализации и уничтижения
+ Необходимо в теге `<beans >` прописать название метода инициализации по умолчанию
  ```
  <beans default-init-method="init">
  
      <bean id=...>
         ....
      </bean>
  
  </beans>
  ```
+ Наличие атрибута default-init-method в атрибуте элемента верхнего уровня <beans/> приводит к тому, что контейнер  
 Spring IoC распознает метод init в классе bean в качестве обратного вызова метода инициализации.
+ Аналогично можно настроить обратные вызовы метода destroy (то есть в XML), используя атрибут default-destroy-method   
элемента верхнего уровня <beans/>.

### Combining Lifecycle Mechanisms
+ Несколько механизмов жизненного цикла, настроенных для одного и того же компонента с различными методами   
инициализации, называются вызываются в следующем порядке:
  + метод анотированный @PostConstruct
  + метод afterPropertiesSet() определенный интерфейсом InitializingBean
  + свой метод init()
+ Методы Destroy вызываются в том же порядке:
  + Методы, аннотированные @PreDestro
  + destroy (), как определено интерфейсом обратного вызова DisposableBean  
  + Настраиваемый метод destroy()
### BeanPostProcessor
+ Реализует пользовательскую логику после инициализации бина
+ Можно создать несколько экземпляров и запускать по очереди  
+ Работает с любыми бинами в одном контейнере
+ ApplicationContext регистрирует постпроцессоры при старте как и бины
+ Для создания необходимо реализовать два метода:
  ```
  public Object postProcessBeforeInitialization(Object bean, String beanName)
  ```
  ```
  public Object postProcessAfterInitialization(Object bean, String beanName)
  ```
+ Для регистрации:
    ```
    <bean class="scripting.InstantiationTracingBeanPostProcessor"/>
    ```  
### BeanFactoryPostProcessor
  




  