<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="listing">
        <h2 class="jh-entity-heading" data-cy="listingDetailsHeading"><span>Listing</span> {{ listing.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ listing.name }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ listing.description }}</span>
          </dd>
          <dt>
            <span>Price</span>
          </dt>
          <dd>
            <span>{{ listing.price }}</span>
          </dd>
          <dt>
            <span>Address</span>
          </dt>
          <dd>
            <span>{{ listing.address }}</span>
          </dd>
          <dt>
            <span>Category</span>
          </dt>
          <dd>
            <span>{{ listing.category }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link v-if="listing.id" :to="{ name: 'ListingEdit', params: { listingId: listing.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./listing-details.component.ts"></script>
