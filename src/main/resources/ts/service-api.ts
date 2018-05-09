export enum RequestMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
}

export interface HttpRequestMapping {
    urlTemplate: string;
    method: RequestMethod;
}

export interface MethodParamMapping {
    paramName: string;
    isRequired: boolean;
    pathVariableName?: string;
    requestParameterName?: string;
    isRequestBody: boolean;
    value: any;
}



