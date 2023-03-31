export interface SunmiNfcPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
