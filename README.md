# Type Script Service Generator
  
Compile time library to auto-generate TypeScript services for Spring MVC controllers and interfaces for all the return and parameter types
to be used in Angular client side projects.

The library relies on Angular HttpClient service to handle the networking layer requiring zero initial special efforts for configuration of 
client-server interaction.

### Key Features

The library supports advanced features of Java type system and Spring Framework:
* Out of the box Spring MVC annotation support
* Integrated [Project Lombok](https://projectlombok.org/) support for property detection
* [Spring Meta annotation processing](https://github.com/spring-projects/spring-framework/wiki/Spring-Annotation-Programming-Model) 
        - create your own annotations by combining the existing ones.
* ```@PathVariable``` support for constructing request urls
* Full set of features for return and parameter types:
  * **Inheritance** - supertypes and interfaces are mirrored as TypeScript interfaces 
  * **Generics** - generic type information is mirrored into TypeScript interfaces
  * **Enums** - represented as named enums in TypeScript
  * Inner classes - are captured as prefixed classes
  * Java 8 Time classes support
* Flexible filtering for input types to prevent _"type number bomb"_
* Configurable replacement for the default HttpClient based network layer implementation with custom client code 

## Design philosophy

1. Minimal upfront config
2. Convention over configuration
3. Customization points for advanced use cases
4. Generate proper TypeScript semantics where they differ from Java  

## Getting started

The library utilizes Java Annotation Processing facilities ([JSR-269](https://www.jcp.org/en/jsr/detail?id=269))  
to process the source code at compile time and generate corresponding TypeScript representations.

This approach ensures support by the majority of the standard Java tools and IDEs, and integrates seamlessly with popular
CI servers and build tools, as it relies on javac for execution and configuration.

Specific setup steps will depend on the build tools used, but simply placing the jar on the class path during compilation
should be sufficient in the default environment.

The recommended way to integrate the library into the project is to ase APT plugin for the popular build tools.

#### Gradle Example

````build.gradle:````
```groovy
plugins {
  id "net.ltgt.apt" version "0.17"
}

//TypeScript endpoint Processing
compileOnly group: "io.github.tri-omega", name: "typescript-service-generator", version: "0.1.6"
annotationProcessor('org.springframework:spring-web')
annotationProcessor group: "io.github.tri-omega", name: "typescript-service-generator", version: "0.1.6"
```

#### Source code 

The minimal required configuration is to mark a controller with the annotation ```@TypeScriptEndpoint```. This will generate
corresponding Angular service class for this controller and type interfaces for any return or parameter types with a default module definition 
   

```SimpleDtoController.java:```
```java
@RestController
@TypeScriptEndpoint //Enables the generation of the TypeScript service for this controller
@RequestMapping(method = RequestMethod.GET, path = "/api/")
public class SimpleDtoController {
   @GetMapping("get")
   public SimpleDto getSimpleDto() {
       return new SimpleDto();
   }
}
```

Source code for [SimpleDto.java](https://github.com/william-frank/typescript-service-generator/blob/master/src/test/resources/org/omega/typescript/processor/test/dto/SimpleDto.java)

As a result this will generate the following TypeScript sources:

```SimpleDtoController.generates.ts:```
```typescript
@Injectable()
export class SimpleDtoController {

    constructor(private httpService:ServiceRequestManager) { }
    
    defaultRequestMapping:HttpRequestMapping = {urlTemplate: '/api/', method: RequestMethod.GET};
    
    public getSimpleDto(): Observable<SimpleDto> {
        const mapping:HttpRequestMapping = {urlTemplate: 'get', method: RequestMethod.GET};
        const params: MethodParamMapping[] = [];
        return this.httpService.execute(this.defaultRequestMapping, mapping, params);
    }
}
```

```SimpleDto.generated.ts:```
```typescript
export interface SimpleDto {
	field1: string;
	field2: number;
	customName: number;
}
```

### Output configuration

The library is configured by placing a ```tsg-config.properties``` file on the with the source code:
```properties 
tsg.output-folder=build/gen/

org.omega.typescript.processor.test=api/service
```

In this example the properties specify that:
1. TypeScript output should be emitted into the folder ```build/gen``` 
2. package ```org.omega.typescript.processor.test``` should be shortened to api/service. _Example_: class name
    ```org.omega.typescript.processor.test.dto.SimpleDto``` will be emitted as ```api/service/dto/SimpleDto.generated.ts```
 
```Note:``` Any unknown property is treated as a package override. 
    
 ### Advanced Configuration properties
 
 A full list of configuration properties:
 
| Property      | Description           | Default   |
| --------------|-----------------------|-----------|
| tsg.output-folder | base folder to emit TypeScript to | tsg-gen/ | 
| tsg.default-module-name | name for the default module | api |
| tsg.http-service-class | Class name for the network layer service | ServiceRequestManager |
| tsg.http-service-include | Import path for the network layer service | tsg-std/ServiceRequestManager |
| tsg.service-includes | Additional imports for service classes. Allows to customize the library type imports | Import {Injectable} from '@angular/core'; import {Observable} from 'rxjs'; |
| tsg.std-api-file-name | File name to emit support library classes to | tsg-std/api.ts |
| tsg.indent.width | Number of spaces to indent generated code blocks | 2 |
| tsg.request-manager-file-name | File name to emit the standard _ServiceRequestManager_ service | tsg-std/ServiceRequestManager.ts |
| tsg.enable-java-time-integration | Enable or disable special type overrides for Java 8 Time classes | true |
| tsg.java-time.zoned-date-time-type | _java.time.ZonedDateTime_ TypeScript alias | number |
| tsg.java-time.local-date-time-type | _java.time.LocalDateTime_ TypeScript alias | string |
| tsg.java-time.time-type | _java.time.LocalTime_ TypeScript alias | string |
| tsg.java-time.date-type | _LocalDate_ TypeScript alias | string |
| tsg.exclude-classes-regex._{uniqueId}_ | A set of regex expressions to exclude classes by name. There can be any number of exclusions, but configuration with same _uniqueId_ overrides defaults (can be used to change defaults). | tsg.exclude-classes-regex.io=java\\.io\\..+ |

## Contacts & Licensing
Author: William Frank

Lincense: [MIT License](https://en.wikipedia.org/wiki/MIT_License)

For questions and suggestiosn please contact at [info@williamfrank.net](mailto:info@williamfrank.net)   