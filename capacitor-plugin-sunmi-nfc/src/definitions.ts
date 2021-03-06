declare module '@capacitor/core' {
  interface PluginRegistry {
    SunmiNfc: SunmiNfcPlugin;
  }
}

export interface SunmiNfcPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  discoverNfcCard(): Promise<{decId: string, reversedDecId: string, hexId: string, reversedHexId: string}>;
}
