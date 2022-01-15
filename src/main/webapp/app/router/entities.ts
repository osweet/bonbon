import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Listing = () => import('@/entities/listing/listing.vue');
// prettier-ignore
const ListingUpdate = () => import('@/entities/listing/listing-update.vue');
// prettier-ignore
const ListingDetails = () => import('@/entities/listing/listing-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/listing',
    name: 'Listing',
    component: Listing,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/listing/new',
    name: 'ListingCreate',
    component: ListingUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/listing/:listingId/edit',
    name: 'ListingEdit',
    component: ListingUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/listing/:listingId/view',
    name: 'ListingView',
    component: ListingDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
