{{/*
Expand the name of the chart.
*/}}
{{- define "voting-app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "voting-app.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "voting-app.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "voting-app.labels" -}}
helm.sh/chart: {{ include "voting-app.chart" . }}
{{ include "voting-app.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "voting-app.selectorLabels" -}}
app.kubernetes.io/name: {{ include "voting-app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the namespace
*/}}
{{- define "voting-app.namespaceName" -}}
{{- default .Release.Namespace .Values.namespace.name }}
{{- end }}

{{/*
Create the name of PostgreSQL
*/}}
{{- define "voting-app.postgresName" -}}
{{- printf "%s-postgres" (include "voting-app.fullname" .) }}
{{- end }}

{{/*
Create the name of PgAdmin
*/}}
{{- define "voting-app.pgadminName" -}}
{{- printf "%s-pgadmin" (include "voting-app.fullname" .) }}
{{- end }}

{{/*
Create the name of HAProxy
*/}}
{{- define "voting-app.haproxyName" -}}
{{- printf "%s-haproxy" (include "voting-app.fullname" .) }}
{{- end }}

{{/*
Create the name of Voting App
*/}}
{{- define "voting-app.appName" -}}
{{- printf "%s-app" (include "voting-app.fullname" .) }}
{{- end }}