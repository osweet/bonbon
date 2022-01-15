<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bonbonApp.listing.home.createOrEditLabel" data-cy="ListingCreateUpdateHeading">Create or edit a Listing</h2>
        <div>
          <div class="form-group" v-if="listing.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="listing.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="listing-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="listing-name"
              data-cy="name"
              :class="{ valid: !$v.listing.name.$invalid, invalid: $v.listing.name.$invalid }"
              v-model="$v.listing.name.$model"
            />
            <div v-if="$v.listing.name.$anyDirty && $v.listing.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.listing.name.maxLength">
                This field cannot be longer than 100 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="listing-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="listing-description"
              data-cy="description"
              :class="{ valid: !$v.listing.description.$invalid, invalid: $v.listing.description.$invalid }"
              v-model="$v.listing.description.$model"
            />
            <div v-if="$v.listing.description.$anyDirty && $v.listing.description.$invalid">
              <small class="form-text text-danger" v-if="!$v.listing.description.maxLength">
                This field cannot be longer than 2000 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="listing-price">Price</label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="listing-price"
              data-cy="price"
              :class="{ valid: !$v.listing.price.$invalid, invalid: $v.listing.price.$invalid }"
              v-model.number="$v.listing.price.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="listing-address">Address</label>
            <input
              type="text"
              class="form-control"
              name="address"
              id="listing-address"
              data-cy="address"
              :class="{ valid: !$v.listing.address.$invalid, invalid: $v.listing.address.$invalid }"
              v-model="$v.listing.address.$model"
            />
            <div v-if="$v.listing.address.$anyDirty && $v.listing.address.$invalid">
              <small class="form-text text-danger" v-if="!$v.listing.address.maxLength">
                This field cannot be longer than 100 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="listing-category">Category</label>
            <input
              type="text"
              class="form-control"
              name="category"
              id="listing-category"
              data-cy="category"
              :class="{ valid: !$v.listing.category.$invalid, invalid: $v.listing.category.$invalid }"
              v-model="$v.listing.category.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.listing.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./listing-update.component.ts"></script>
