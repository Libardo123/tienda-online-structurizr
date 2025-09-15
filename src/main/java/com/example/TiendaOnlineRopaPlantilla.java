package com.example;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.Shape;
import com.structurizr.view.Styles;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

public class TiendaOnlineRopaPlantilla {

        public static void main(String[] args) throws Exception {

                Workspace workspace = new Workspace("Tienda Online de Ropa",
                                "Modelo C4 de la tienda online con servicios externos y API Key");

                Model model = workspace.getModel();

                // Personas
                Person cliente = model.addPerson("Cliente", "Usuario que compra ropa en la tienda online.");
                Person administrador = model.addPerson("Administrador", "Gestiona catálogo, pedidos y usuarios.");

                // Sistema principal
                SoftwareSystem tiendaOnline = model.addSoftwareSystem("Tienda Online",
                                "Plataforma web y móvil para la venta de ropa.");
                cliente.uses(tiendaOnline,
                                "Compra ropa, consulta pedidos y actualiza su perfil.", "HTTPS");
                administrador.uses(tiendaOnline,
                                "Administra productos, usuarios y pedidos.", "HTTPS");

                // Sistemas externos
                SoftwareSystem pasarelaPago = model.addSoftwareSystem("Pasarela de Pago",
                                "Procesa pagos con tarjeta de crédito y débito.");
                SoftwareSystem servicioEnvio = model.addSoftwareSystem("Servicio de Envío",
                                "Gestiona la logística y tracking de pedidos.");
                SoftwareSystem proveedorNotificaciones = model.addSoftwareSystem("Proveedor de Notificaciones",
                                "Envío de emails y SMS transaccionales.");

                tiendaOnline.uses(pasarelaPago, "Procesa pagos de clientes con API Key", "API REST (HTTPS)");
                tiendaOnline.uses(servicioEnvio, "Solicita envíos y obtiene tracking de pedidos",
                                "API REST (HTTPS, Auth Token)");
                tiendaOnline.uses(proveedorNotificaciones, "Envía emails y SMS de confirmación", "API REST (HTTPS)");

                // Contenedores
                Container webApp = tiendaOnline.addContainer("Aplicación Web",
                                "Permite a los clientes comprar ropa mediante navegador.",
                                "Angular / React");
                Container mobileApp = tiendaOnline.addContainer("Aplicación Móvil",
                                "Permite a los clientes comprar ropa desde su celular.",
                                "Flutter / Kotlin / Swift");
                Container api = tiendaOnline.addContainer("API Backend",
                                "Gestiona la lógica de negocio de la tienda y expone APIs REST.", "Java Spring Boot");
                Container database = tiendaOnline.addContainer("Base de Datos",
                                "Almacena usuarios, productos y pedidos.", "MySQL");
                database.addTags("Database");

                Container identityProvider = tiendaOnline.addContainer("Proveedor de Identidad",
                                "Gestiona la autenticación de clientes y administradores con JWT y API Key.",
                                "Keycloak / Auth0");

                // CÓDIGO DEL EJERCICIO 2
                Container servicioRecomendaciones = tiendaOnline.addContainer("Servicio de Recomendaciones",
                                "Sugiere productos a los clientes", "Python Flask");
                Container servicioAnalisis = tiendaOnline.addContainer("Servicio de Análisis",
                                "Recolecta y analiza datos de uso", "Apache Kafka");

                // Relaciones contenedores
                cliente.uses(webApp, "Compra ropa", "HTTPS");
                cliente.uses(mobileApp, "Compra ropa", "HTTPS");
                webApp.uses(api, "Consume servicios REST", "JSON/HTTPS");
                mobileApp.uses(api, "Consume servicios REST", "JSON/HTTPS");
                api.uses(database, "Lee y escribe datos", "JDBC");
                api.uses(identityProvider, "Validación de credenciales con API Key y JWT", "OpenID Connect");
                administrador.uses(webApp, "Administra catálogo y usuarios", "HTTPS");

                // Relaciones con los nuevos contenedores del Ejercicio 2
                webApp.uses(servicioRecomendaciones, "Solicita recomendaciones");
                servicioRecomendaciones.uses(database, "Lee datos de productos");
                servicioAnalisis.uses(database, "Lee datos para análisis");

                // CÓDIGO AÑADIDO PARA EL EJERCICIO 3
                // Componentes dentro del Servicio de Recomendaciones
                Component motorRecomendaciones = servicioRecomendaciones.addComponent("Motor de Recomendaciones",
                                "Algoritmo que genera recomendaciones", "Python");
                Component apiRecomendaciones = servicioRecomendaciones.addComponent("API de Recomendaciones",
                                "Expone recomendaciones vía REST", "Python Flask");

                // Relaciones con los nuevos componentes
                webApp.uses(apiRecomendaciones, "Solicita recomendaciones");
                apiRecomendaciones.uses(motorRecomendaciones, "Obtiene recomendaciones");
                motorRecomendaciones.uses(database, "Lee datos de productos y historial de compras");

                // CÓDIGO AÑADIDO PARA EL EJERCICIO 4
                // Componentes dentro de la API Backend (se añaden al contenedor `api`)
                Component gestionUsuarios = api.addComponent("Gestión de Usuarios",
                                "Registro, login y roles", "Java");
                Component userController = api.addComponent("UserController",
                                "Maneja solicitudes HTTP relacionadas con usuarios", "Spring MVC");
                Component userService = api.addComponent("UserService",
                                "Lógica de negocio para usuarios", "Servicio");
                Component userRepository = api.addComponent("UserRepository",
                                "Acceso a base de datos para usuarios", "Repositorio");
                Component userModel = api.addComponent("User",
                                "Modelo de datos para usuario", "Entidad");

                // Relaciones entre las clases de código
                userController.uses(userService, "Llama a");
                userService.uses(userRepository, "Usa para persistencia");
                userRepository.uses(userModel, "Lee y escribe");

                // Relaciones adicionales que faltaban
                gestionUsuarios.uses(userController, "Usa para controlar las solicitudes");
                gestionUsuarios.uses(userService, "Usa la lógica de negocio");
                gestionUsuarios.uses(userRepository, "Usa para persistencia");
                gestionUsuarios.uses(identityProvider, "Valida credenciales con JWT");

                // Vistas
                ViewSet views = workspace.getViews();

                SystemContextView contextView = views.createSystemContextView(
                                tiendaOnline, "Contexto", "Vista de contexto de la tienda online.");
                contextView.addAllSoftwareSystems();
                contextView.addAllPeople();

                ContainerView containerView = views.createContainerView(
                                tiendaOnline, "Contenedores", "Vista de contenedores de la tienda online.");
                containerView.addAllPeople();
                containerView.addAllContainers();

                // Añadir una vista para los componentes del Servicio de Recomendaciones
                ComponentView componentesRecomendacionesView = views.createComponentView(
                                servicioRecomendaciones, "Componentes - Servicio de Recomendaciones",
                                "Vista de componentes del Servicio de Recomendaciones.");
                componentesRecomendacionesView.addAllComponents();

                // Añadir una vista de componentes para la Gestión de Usuarios
                ComponentView gestionUsuariosView = views.createComponentView(
                                api, "Código - Gestión de Usuarios",
                                "Vista de código del componente de Gestión de Usuarios.");
                gestionUsuariosView.add(gestionUsuarios);
                gestionUsuariosView.add(userController);
                gestionUsuariosView.add(userService);
                gestionUsuariosView.add(userRepository);
                gestionUsuariosView.add(userModel);
                gestionUsuariosView.add(identityProvider);

                // Estilos
                Styles styles = views.getConfiguration().getStyles();

                styles.addElementStyle(Tags.PERSON)
                                .background("#08427b")
                                .color("#ffffff")
                                .shape(Shape.Person);

                styles.addElementStyle(Tags.SOFTWARE_SYSTEM)
                                .background("#1168bd")
                                .color("#ffffff");

                styles.addElementStyle(Tags.CONTAINER)
                                .background("#438dd5")
                                .color("#ffffff");

                styles.addElementStyle("Database")
                                .shape(Shape.Cylinder)
                                .background("#ff6666")
                                .color("#ffffff");

                // Estilos de componentes
                styles.addElementStyle(Tags.COMPONENT)
                                .background("#85bbf0")
                                .color("#000000");

                styles.addElementStyle("Servicio")
                                .background("#f4a261")
                                .shape(Shape.RoundedBox);

                styles.addElementStyle("Repositorio")
                                .background("#2a9d8f")
                                .shape(Shape.RoundedBox);

                styles.addElementStyle("Entidad")
                                .background("#e76f51")
                                .shape(Shape.RoundedBox);

                styles.addElementStyle("Servicio externo")
                                .background("#264653")
                                .color("#ffffff")
                                .shape(Shape.Cylinder);

                // CÓDIGO PARA SUBIR A STRUCTURIZR CLOUD
                long workspaceld = 106127;
                String apiKey = "a2a6090a-4e16-459e-b3cf-f241ca8e2f2d";
                String apiSecret = "5d7d3e5b-97c7-4190-974a-1fbfbe54de1a";

                StructurizrClient structurizrClient = new StructurizrClient(apiKey, apiSecret);
                structurizrClient.putWorkspace(workspaceld, workspace);
                System.out.println("Workspace subido a Structurizr Cloud con éxito.");
        }
}