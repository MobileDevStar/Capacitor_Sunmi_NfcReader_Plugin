import { WebPlugin } from '@capacitor/core';

import type { SunmiNfcPlugin } from './definitions';

export class SunmiNfcWeb extends WebPlugin implements SunmiNfcPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
