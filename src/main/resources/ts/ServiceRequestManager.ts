import {Injectable} from "@angular/core";
import {HttpClient, HttpParams} from '@angular/common/http';
import {HttpRequestMapping, MethodParamMapping, RequestMethod} from "./api";
import {Observable} from "rxjs/internal/Observable";
import {st} from "@angular/core/src/render3";
import {error} from "@angular/compiler/src/util";


@Injectable()
export class ServiceRequestManager {

  constructor(private httpClient:HttpClient) { }

  public execute<T>(defaultMapping:HttpRequestMapping, requestMapping:HttpRequestMapping, params: MethodParamMapping[])
    : Observable<T> {
    const urlTemplate:string = this.concatUrl(defaultMapping.urlTemplate, requestMapping.urlTemplate, params);
    const httpParams = this.prepareHttpParams(params);
    let body = this.extractBody(params);
    let method:string = requestMapping.method ? requestMapping.method : defaultMapping.method;

    return this.httpClient.request<T>(method, urlTemplate, {
      params: httpParams,
      body: body
    });
  }

  private extractBody(params: MethodParamMapping[]) {
    const bodyParams = params
      .filter(p => p.isRequestBody)
      .filter(p => p.value)
    ;
    if (bodyParams.length > 1) {
      throw error("Unable to handle multiple body params. Received " + bodyParams.length + " body arguments");
    }

    let body: any = bodyParams.length > 0 ? bodyParams[0] : null;
    return body;
  }

  private prepareHttpParams(params: MethodParamMapping[]) {
    const httpParams: HttpParams = new HttpParams();
    const requestParams = params.filter(p => p.requestParameterName && (!p.isRequestBody));
    requestParams.forEach(p => httpParams.append(p.requestParameterName, p.value));
    return httpParams;
  }

  private concatUrl(baseMapping: string, requestMapping: string, params: MethodParamMapping[]):string {
    let url: string = baseMapping;
    if (!url.endsWith("/")) {
      url += "/";
    }
    if (requestMapping.startsWith("/")) {
      url += requestMapping.substring(1);
    } else {
      url += requestMapping;
    }

    params
      .filter(p => p.pathVariableName)
      .forEach(p => {
        url = url.replace("{" + p.pathVariableName + "}", encodeURI(p.value));
      });

    return url;
  }
}
