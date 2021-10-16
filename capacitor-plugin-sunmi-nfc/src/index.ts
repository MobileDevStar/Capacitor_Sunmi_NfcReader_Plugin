import { registerPlugin } from '@capacitor/core';

import type { SunmiNfcPlugin } from './definitions';

const SunmiNfc = registerPlugin<SunmiNfcPlugin>('SunmiNfc', {
  web: () => import('./web').then(m => new m.SunmiNfcWeb()),
});

export * from './definitions';
export { SunmiNfc };
